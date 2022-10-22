/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.base.data.Strings;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.util.StringUtil;
import de.elbe5.defecttracker.DefectFopBean;
import de.elbe5.defecttracker.location.PlanImageData;
import de.elbe5.file.FileBean;
import de.elbe5.request.SessionRequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

import java.time.LocalDateTime;

public class DefectPdfBean extends DefectFopBean {

    private static DefectPdfBean instance = null;

    public static DefectPdfBean getInstance() {
        if (instance == null) {
            instance = new DefectPdfBean();
        }
        return instance;
    }

    // defect

    public BinaryFile getDefectPdfFile(DefectData data, SessionRequestData rdata){
        LocalDateTime now=getServerTime();
        StringBuilder sb=new StringBuilder();
        sb.append("<root>");
        addDefectHeaderXml(sb,data);
        addDefectXml(sb,data,rdata.getSessionHost());
        for (DefectCommentData commnet : data.getComments()){
            addDefectCommentXml(sb, data, commnet, rdata.getSessionHost());
        }
        addDefectFooterXml(sb,data,now);
        sb.append("</root>");
        //System.out.println(sb.toString());
        String fileName="report-of-defect-" + data.getDisplayId() + "-" + StringUtil.toHtmlDateTime(now).replace(' ','-')+".pdf";
        return getPdf(sb.toString(), "pdf.xsl", fileName);
    }

    private void addDefectHeaderXml(StringBuilder sb, DefectData data) {
        sb.append("<header><title>");
        sb.append(Strings.xml("_report"));
        sb.append(": ");
        sb.append(xml(data.getProjectName()));
        sb.append(", ");
        sb.append(xml(data.getLocationName()));
        sb.append(", ");
        sb.append(xml(data.getDisplayName()));
        sb.append("</title></header>");
    }

    private void addDefectFooterXml(StringBuilder sb, DefectData data, LocalDateTime now) {
        sb.append("<footer><date>");
        sb.append(xml(StringUtil.toHtmlDateTime(now)));
        sb.append("</date></footer>");
    }

    private void addDefectXml(StringBuilder sb, DefectData data, String host) {
        sb.append("<defect>");
        addLabeledContent(sb,Strings.string("_description"),data.getDescription());
        addLabeledContent(sb,Strings.string("_id"),Integer.toString(data.getDisplayId()));
        UserData user= UserCache.getUser(data.getCreatorId());
        addLabeledContent(sb,Strings.string("_creator"),user.getName());
        addLabeledContent(sb,Strings.string("_creationDate"),StringUtil.toHtmlDateTime(data.getCreationDate()));
        addLabeledContent(sb,Strings.string("_phase"),Strings.string(data.getPhase()));
        addLabeledContent(sb,Strings.string("_state"),Strings.string(data.getState()));
        addLabeledContent(sb,Strings.string("_assigned"),data.getAssignedName());
        addLabeledContent(sb,Strings.string("_lot"),data.getLot());
        addLabeledContent(sb,Strings.string("_dueDate1"),StringUtil.toHtmlDate(data.getDueDate1()));
        addLabeledContent(sb,Strings.string("_dueDate2"),StringUtil.toHtmlDate(data.getDueDate2()));
        addLabeledContent(sb,Strings.string("_closeDate"),StringUtil.toHtmlDate(data.getCloseDate()));
        PlanImageData plan = FileBean.getInstance().getFile(data.getPlanId(),true,PlanImageData.class);
        byte[] redarrowBytes = FileBean.getInstance().getImageBytes("redarrow.png");
        BinaryFile file = plan.createCroppedDefectPlan(redarrowBytes, data.getId(), data.getPositionX(), data.getPositionY());
        addLabeledImage(sb,Strings.string("_position"), file,"5.0cm");
        addLabeledContent(sb,Strings.string("_positionComment"),data.getPositionComment());
        for (DefectImageData image : data.getFiles(DefectImageData.class)){
            file = FileBean.getInstance().getBinaryFile(image.getId());
            addLabeledImage(sb,Strings.string("_image"),file,"5.0cm");
        }
        sb.append("</defect>");
    }

    private void addDefectCommentXml(StringBuilder sb, DefectData defect, DefectCommentData data, String host) {
        sb.append("<comment>");
        sb.append("<title>").append(xml(data.geTitle())).append("</title>");
        UserData user= UserCache.getUser(data.getCreatorId());
        addLabeledContent(sb,Strings.string("_comment"),data.getComment());
        for (DefectCommentImageData image : defect.getFiles(DefectCommentImageData.class)){
            if (image.getCommentId()==data.getId()) {
                BinaryFile file = FileBean.getInstance().getBinaryFile(image.getId());
                addLabeledImage(sb, Strings.string("_image"), file, "5.0cm");
            }
        }
        sb.append("</comment>");
    }

}
