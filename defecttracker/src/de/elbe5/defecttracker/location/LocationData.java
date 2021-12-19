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
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.defecttracker.project.ProjectData;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationData extends ContentData {

    private static final List<String> contentTypes=new ArrayList<>();
    private static final List<String> documentTypes=new ArrayList<>();
    private static final List<String> imageTypes=new ArrayList<>();

    static{
        contentTypes.add(DefectData.class.getSimpleName());
        documentTypes.add(DocumentData.class.getSimpleName());
        imageTypes.add(ImageData.class.getSimpleName());
        imageTypes.add(PlanImageData.class.getSimpleName());
    }

    protected int projectId=0;

    PlanImageData plan = null;

    public LocationData() {
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    // set plans

    public void initializeChildren() {
        super.initializeChildren();
        plan=null;
        List<PlanImageData> candidates = getFiles(PlanImageData.class);
        if (candidates.size()==1)
            plan=candidates.get(0);
    }

    public PlanImageData getPlan() {
        return plan;
    }

    @Override
    public String getContentDataJsp() {
        return "/WEB-INF/_jsp/defecttracker/location/editContentData.ajax.jsp";
    }

    @Override
    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        Writer writer = context.getOut();
        writer.write("<div id=\"pageContent\" class=\"viewArea\">");
        context.include("/WEB-INF/_jsp/defecttracker/location/location.jsp");
        writer.write("</div>");
    }

    //used in jsp
    public void displayTreeContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        if (hasUserReadRight(rdata)) {
            //backup
            ContentData currentContent=rdata.getCurrentContent();
            rdata.setCurrentRequestContent(this);
            context.include("/WEB-INF/_jsp/defecttracker/location/treeContent.inc.jsp", true);
            //restore
            rdata.setCurrentRequestContent(currentContent);
        }
    }

    @Override
    public List<String> getChildClasses(){
        return contentTypes;
    }

    @Override
    public List<String> getDocumentClasses(){
        return documentTypes;
    }

    @Override
    public boolean hasUserReadRight(SessionRequestData rdata) {
        return ViewFilter.getFilter(rdata).hasProjectReadRight(getProjectId());
    }

    public boolean hasUserReadRight(ViewFilter filter) {
        return filter.hasProjectReadRight(getProjectId());
    }

    @Override
    public boolean hasUserEditRight(SessionRequestData rdata) {
        return rdata.hasSystemRight(SystemZone.CONTENTEDIT);
    }

    public boolean hasUserAnyEditRight(SessionRequestData rdata) {
        return rdata.hasSystemRight(SystemZone.CONTENTEDIT) || rdata.hasSystemRight(SystemZone.SPECIFICCONTENTEDIT);
    }

    @Override
    public List<String> getImageClasses(){
        return imageTypes;
    }

    // multiple data

    @Override
    public void setCreateValues(ContentData parent, RequestData rdata) {
        super.setCreateValues(parent,rdata);
        if (!(parent instanceof ProjectData)){
            Log.error("parent of location part page should be project page");
            return;
        }
        setProjectId(parent.getId());
        ProjectData project=(ProjectData)parent;
    }

    @Override
    public void readCreateRequestData(SessionRequestData rdata) {
        setDisplayName(rdata.getString("displayName").trim());
        setName(StringUtil.toSafeWebName(getDisplayName()));
        setDescription(rdata.getString("description"));
        setNavType(ContentData.NAV_TYPE_HEADER);
        setActive(rdata.getBoolean("active"));
        BinaryFile file = rdata.getFile("file");
        if (file != null){
            plan = new PlanImageData();
            plan.setCreateValues(this,rdata);
            plan.createFromBinaryFile(file, PlanImageData.STD_SIZE, PlanImageData.STD_SIZE, plan.getMaxPreviewWidth(), plan.getMaxPreviewHeight(), false);
            plan.setDisplayName(Strings.string("_plan",rdata.getSessionLocale()));
        }
        if (getDisplayName().isEmpty()) {
            rdata.addIncompleteField("displayName");
        }
    }

    @Override
    public void readUpdateRequestData(SessionRequestData rdata) {
        setDisplayName(rdata.getString("displayName").trim());
        setName(StringUtil.toSafeWebName(getDisplayName()));
        setDescription(rdata.getString("description"));
        setNavType(ContentData.NAV_TYPE_HEADER);
        BinaryFile file = rdata.getFile("file");
        if (file != null && plan == null){
            plan = new PlanImageData();
            plan.setCreateValues(this,rdata);
            plan.createFromBinaryFile(file, PlanImageData.STD_SIZE, PlanImageData.STD_SIZE, plan.getMaxPreviewWidth(), plan.getMaxPreviewHeight(), false);
            plan.setDisplayName(Strings.string("_plan",rdata.getSessionLocale()));
        }
        setActive(rdata.getBoolean("active"));
        if (getDisplayName().isEmpty()) {
            rdata.addIncompleteField("displayName");
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJson(Locale locale){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("name",getDisplayName());
        json.put("description",getDescription());
        return json;
    }

}
