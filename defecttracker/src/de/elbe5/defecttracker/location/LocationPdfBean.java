/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.location;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.util.StringUtil;
import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.DefectFopBean;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.defecttracker.project.ProjectData;
import de.elbe5.file.ImageBean;
import de.elbe5.request.SessionRequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class LocationPdfBean extends DefectFopBean {

    private static LocationPdfBean instance = null;

    public static LocationPdfBean getInstance() {
        if (instance == null) {
            instance = new LocationPdfBean();
        }
        return instance;
    }

    public BinaryFile getLocationReport(int locationId, SessionRequestData rdata){
        Locale locale=rdata.getLocale();
        LocalDateTime now=getServerTime();
        LocationData location= ContentCache.getContent(locationId,LocationData.class);
        if (location==null)
            return null;
        StringBuilder sb=new StringBuilder();
        sb.append("<root>");
        addLocationHeaderXml(sb,location,locale);
        sb.append("<location>");
        List<DefectData> defects = ViewFilter.getFilter(rdata).getLocationDefects(location.getId());
        addLocationDefectsXml(sb,location, defects, locale);
        PlanImageData plan = location.getPlan();
        if (plan!=null) {
            PlanImageData fullplan = ImageBean.getInstance().getFile(plan.getId(), true, PlanImageData.class);
            byte[] redarrowBytes = LocationBean.getInstance().getImageBytes("redarrow.png");
            defects = ViewFilter.getFilter(rdata).getLocationDefects(location.getId());
            BinaryFile file = fullplan.createLocationDefectPlan(redarrowBytes, defects, 1);
            addLocationPlanXml(sb, location, plan, file, locale);
        }
        sb.append("</location>");
        addLocationFooterXml(sb,location,now,locale);
        sb.append("</root>");
        //System.out.println(sb.toString());
        String fileName="report-of-location-defects-" + location.getId() + "-" + StringUtil.toHtmlDateTime(now,locale).replace(' ','-')+".pdf";
        return getPdf(sb.toString(), "pdf.xsl", fileName);
    }

    private void addLocationHeaderXml(StringBuilder sb, LocationData location, Locale locale) {
        ProjectData project=ContentCache.getContent(location.getProjectId(),ProjectData.class);
        assert(project!=null);
        sb.append("<header><title>");
        sb.append(Strings.xml("_reports",locale));
        sb.append(": ");
        sb.append(xml(project.getDisplayName()));
        sb.append(", ");
        sb.append(xml(location.getDisplayName()));
        sb.append("</title></header>");
    }

    private void addLocationFooterXml(StringBuilder sb, LocationData info, LocalDateTime now, Locale locale) {
        sb.append("<footer><date>");
        sb.append(xml(StringUtil.toHtmlDateTime(now,locale)));
        sb.append("</date></footer>");
    }

    private void addLocationDefectsXml(StringBuilder sb, LocationData data, List<DefectData> defects, Locale locale) {
        for (DefectData defect : defects){
            sb.append("<locationdefect>");
            sb.append("<title>").append(Strings.xml("_id",locale)).append(" ").append(defect.getDisplayId()).append("</title>");
            sb.append("<description>").append(xml(defect.getDescription())).append("</description>");
            sb.append("<defectrow>");
            sb.append("<label1>").append(Strings.xml("_id",locale)).append("</label1><content1>").append(defect.getDisplayId()).append("</content1>");
            sb.append("<label2>").append(Strings.xml("_phase",locale)).append("</label2><content2>").append(Strings.xml(defect.getPhase(),locale)).append("</content2>");
            sb.append("</defectrow>");
            sb.append("<defectrow>");
            UserData user= UserCache.getUser(defect.getCreatorId());
            sb.append("<label1>").append(Strings.xml("_creator",locale)).append("</label1><content1>").append(xml(user.getName())).append("</content1>");
            sb.append("<label2>").append(Strings.xml("_creationDate",locale)).append("</label2><content2>").append(StringUtil.toHtmlDateTime(defect.getCreationDate(),locale)).append("</content2>");
            sb.append("</defectrow>");
            sb.append("<defectrow>");
            sb.append("<label1>").append(Strings.xml("_assigned",locale)).append("</label1><content1>").append(xml(defect.getAssignedName())).append("</content1>");
            sb.append("<label2>").append(Strings.xml("_lot",locale)).append("</label2><content2>").append(xml(defect.getLot())).append("</content2>");
            sb.append("</defectrow>");
            sb.append("<defectrow>");
            sb.append("<label1>").append(Strings.xml("_dueDate1",locale)).append("</label1><content1>").append(StringUtil.toHtmlDate(defect.getDueDate1(),locale)).append("</content1>");
            sb.append("<label2>").append(Strings.xml("_dueDate2",locale)).append("</label2><content2>").append(StringUtil.toHtmlDate(defect.getDueDate2(),locale)).append("</content2>");
            sb.append("</defectrow>");
            sb.append("<defectrow>");
            sb.append("<label1>").append(Strings.xml("_state",locale)).append("</label1><content1>").append(Strings.xml(defect.getState(),locale)).append("</content1>");
            sb.append("<label2>").append(Strings.xml("_closeDate",locale)).append("</label2><content2>").append(StringUtil.toHtmlDate(defect.getCloseDate(),locale)).append("</content2>");
            sb.append("</defectrow>");
            sb.append("<defectrow>");
            sb.append("<label1>").append(Strings.xml("_positionComment",locale)).append("</label1><content1>").append(xml(defect.getPositionComment())).append("</content1>");
            sb.append("<label2>").append("</label2><content2>").append("</content2>");
            sb.append("</defectrow>");
            sb.append("</locationdefect>");
        }
    }

    private void addLocationPlanXml(StringBuilder sb, LocationData data, PlanImageData plan, BinaryFile file, Locale locale) {
        sb.append("<locationplan>");
        sb.append("<name>").append(xml(plan.getDisplayName())).append("</name>");
        sb.append("<src>").append(getBase64SrcString(file)).append("</src>");
        sb.append("</locationplan>");
    }


}
