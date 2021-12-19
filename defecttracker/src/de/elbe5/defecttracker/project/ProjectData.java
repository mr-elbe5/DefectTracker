/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.project;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.util.StringUtil;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
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

public class ProjectData extends ContentData {

    public static final String PHASE_PREAPPROVE = "PREAPPROVE";
    public static final String PHASE_APPROVE = "APPROVE";
    public static final String PHASE_WARRANTY = "WARRANTY";

    private static List<String> contentTypes=new ArrayList<>();
    private static List<String> documentTypes=new ArrayList<>();
    private static List<String> imageTypes=new ArrayList<>();

    static{
        contentTypes.add(LocationData.class.getSimpleName());
        documentTypes.add(DocumentData.class.getSimpleName());
        imageTypes.add(ImageData.class.getSimpleName());
    }

    protected int groupId=0;
    protected String phase=PHASE_PREAPPROVE;

    public ProjectData() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
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
    public List<String> getImageClasses(){
        return imageTypes;
    }

    @Override
    public boolean hasUserReadRight(SessionRequestData rdata) {
        return ViewFilter.getFilter(rdata).hasProjectReadRight(getId());
    }

    @Override
    public boolean hasUserEditRight(SessionRequestData rdata) {
        return rdata.hasSystemRight(SystemZone.CONTENTEDIT);
    }
    // view

    @Override
    public String getContentDataJsp() {
        return "/WEB-INF/_jsp/defecttracker/project/editContentData.ajax.jsp";
    }

    @Override
    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        Writer writer = context.getOut();
        writer.write("<div id=\"pageContent\" class=\"viewArea\">");
        context.include("/WEB-INF/_jsp/defecttracker/project/project.jsp");
        writer.write("</div>");
    }

    //used in jsp
    public void displayTreeContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        if (hasUserReadRight(rdata)) {
            //backup
            ContentData currentContent=rdata.getCurrentContent();
            rdata.setCurrentRequestContent(this);
            context.include("/WEB-INF/_jsp/defecttracker/project/treeContent.inc.jsp", true);
            //restore
            rdata.setCurrentRequestContent(currentContent);
        }
    }

    // multiple data

    @Override
    public void readRequestData(SessionRequestData rdata) {
        setDisplayName(rdata.getString("displayName").trim());
        setName(StringUtil.toSafeWebName(getDisplayName()));
        setDescription(rdata.getString("description"));
        setGroupId(rdata.getInt("groupId"));
        setPhase(rdata.getString("phase"));
        setActive(rdata.getBoolean("active"));
        setNavType(ContentData.NAV_TYPE_HEADER);
        if (getDisplayName().isEmpty()) {
            rdata.addIncompleteField("displayName");
        }
        if (getGroupId()==0) {
            rdata.addIncompleteField("groupId");
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJson(Locale locale){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("name",getDisplayName());
        json.put("description",getDescription());
        json.put("phase", Strings.string(getPhase(),locale));
        return json;
    }

}
