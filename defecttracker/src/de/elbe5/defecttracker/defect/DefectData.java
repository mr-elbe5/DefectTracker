/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.DateUtil;
import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.content.ContentData;
import de.elbe5.defecttracker.project.ProjectData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class DefectData extends ContentData {

    public static final String STATE_OPEN = "OPEN";
    public static final String STATE_DISPUTED = "DISPUTED";
    public static final String STATE_REJECTED = "REJECTED";
    public static final String STATE_DONE = "DONE";

    private static final List<String> contentTypes = new ArrayList<>();
    private static final List<String> documentTypes = new ArrayList<>();
    private static final List<String> imageTypes = new ArrayList<>();

    static {
        documentTypes.add(DefectDocumentData.class.getSimpleName());
        imageTypes.add(DefectImageData.class.getSimpleName());
    }

    protected int displayId = 0;
    protected int locationId = 0;
    protected int projectId = 0;
    protected int planId = 0;
    protected int assignedId = 0;
    protected String lot = "";
    protected String phase = ProjectData.PHASE_PREAPPROVE;
    protected String state = STATE_OPEN;
    protected int costs = 0;
    protected int positionX = 0; // Percent * 100
    protected int positionY = 0; // Percent * 100
    protected String positionComment = "";
    protected LocalDate dueDate1 = null;
    protected LocalDate dueDate2 = null;
    protected LocalDate closeDate = null;

    protected List<DefectCommentData> comments = new ArrayList<>();

    // runtime

    protected String projectName="";
    protected String locationName="";

    // base data

    public String getCreatorName(){
        UserData user=UserCache.getUser(getCreatorId());
        if (user!=null)
            return user.getName();
        return "";
    }

    @Override
    public String getName(){
        return "defect-"+getDisplayId();
    }

    @Override
    public String getDisplayName(){
        return "ID "+getDisplayId();
    }

    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocationId() {
        return parentId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(int assignedId) {
        this.assignedId = assignedId;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isClosed(){
        return getCloseDate()!=null;
    }

    public int getCosts() {
        return costs;
    }


    public String getCostsString() {
        return costs==0 ? "" : Integer.toString(costs);
    }

    public void setCosts(int costs) {
        this.costs = costs;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public String getPositionComment() {
        return positionComment;
    }

    public void setPositionComment(String positionComment) {
        this.positionComment = positionComment;
    }

    public LocalDate getDueDate1() {
        return dueDate1;
    }

    public void setDueDate1(LocalDate dueDate1) {
        this.dueDate1 = dueDate1;
    }

    public LocalDate getDueDate2() {
        return dueDate2;
    }

    public void setDueDate2(LocalDate dueDate2) {
        this.dueDate2 = dueDate2;
    }

    public LocalDate getDueDate() {
        return dueDate2 != null ? dueDate2 : dueDate1;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public List<DefectCommentData> getComments() {
        return comments;
    }

    public String getProjectName() {
        if (projectName.isEmpty()){
            ProjectData data= ContentCache.getContent(projectId,ProjectData.class);
            if (data!=null)
                projectName=data.getDisplayName();
        }
        return projectName;
    }

    public String getLocationName() {
        if (locationName.isEmpty()){
            LocationData data= ContentCache.getContent(locationId,LocationData.class);
            if (data!=null)
                locationName=data.getDisplayName();
        }
        return locationName;
    }

    public String getAssignedName() {
        if (assignedId==0)
            return "";
        UserData data= UserCache.getUser(assignedId);
        if (data!=null)
            return data.getName();
        return "";
    }

    @Override
    public List<String> getChildClasses() {
        return contentTypes;
    }

    @Override
    public List<String> getDocumentClasses() {
        return documentTypes;
    }

    @Override
    public List<String> getImageClasses() {
        return imageTypes;
    }

    @Override
    public boolean hasUserReadRight(SessionRequestData rdata) {
        return ViewFilter.getFilter(rdata).hasProjectReadRight(getProjectId()) && (rdata.hasSystemRight(SystemZone.CONTENTEDIT) || rdata.getUserId()==getAssignedId());
    }

    public boolean hasUserReadRight(ViewFilter filter, UserData user) {
        return filter.hasProjectReadRight(getProjectId()) && (user.hasSystemRight(SystemZone.CONTENTEDIT) || user.getId()==getAssignedId());
    }

    @Override
    public boolean hasUserEditRight(SessionRequestData rdata) {
        return rdata.hasSystemRight(SystemZone.CONTENTEDIT);
    }

    public boolean hasUserAnyEditRight(SessionRequestData rdata) {
        return rdata.hasSystemRight(SystemZone.CONTENTEDIT) || rdata.hasSystemRight(SystemZone.SPECIFICCONTENTEDIT);
    }

    // view

    @Override
    public String getContentDataJsp() {
        return "/WEB-INF/_jsp/defecttracker/defect/editContentData.ajax.jsp";
    }

    @Override
    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        Writer writer = context.getOut();
        writer.write("<div id=\"pageContent\" class=\"viewArea\">");
        if (ContentData.VIEW_TYPE_EDIT.equals(getViewType())) {
            if (isNew())
                context.include("/WEB-INF/_jsp/defecttracker/defect/createDefect.jsp");
            else
                context.include("/WEB-INF/_jsp/defecttracker/defect/editDefect.jsp");
        } else {
            context.include("/WEB-INF/_jsp/defecttracker/defect/defect.jsp");
        }
        writer.write("</div>");
    }

    // multiple data

    @Override
    public void setCreateValues(ContentData parent, RequestData rdata) {
        super.setCreateValues(parent, rdata);
        if (!(this.parent instanceof LocationData)) {
            Log.error("parent of defect page should be location page");
            return;
        }
        setDisplayId(DefectBean.getInstance().getNextDisplayId());
        LocationData location = (LocationData) this.parent;
        ProjectData project = (ProjectData) location.getParent();
        setLocationId(location.getId());
        setProjectId(project.getId());
        setState(STATE_OPEN);
        setPhase(project.getPhase());
        setNavType(NAV_TYPE_NONE);
        setPlanId(location.getPlan() == null ? 0 : location.getPlan().getId());
    }

    @Override
    public void readFrontendCreateRequestData(SessionRequestData rdata) {
        Locale locale = rdata.getLocale();
        readCommonRequestData(rdata);
        setDescription(rdata.getString("description").trim());
        setDueDate1(rdata.getDate("dueDate1", locale));
        setPositionX(rdata.getInt("positionX"));
        setPositionY(rdata.getInt("positionY"));
        setPositionComment(rdata.getString("positionComment"));
        if (getDescription().isEmpty()) {
            rdata.addIncompleteField("description");
        }
        if (getAssignedId()==0) {
            rdata.addIncompleteField("assigned");
        }
        if (getDueDate()==null) {
            rdata.addIncompleteField("dueDate1");
        }
    }

    @Override
    public void readFrontendUpdateRequestData(SessionRequestData rdata) {
        Locale locale = rdata.getLocale();
        readCommonRequestData(rdata);
        setDueDate2(rdata.getDate("dueDate2", locale));
        if (getAssignedId()==0) {
            rdata.addIncompleteField("assigned");
        }
    }

    public void readCommonRequestData(SessionRequestData rdata) {
        setAssignedId(rdata.getInt("assigned"));
        setLot(rdata.getString("lot"));
        setCosts(rdata.getInt("costs"));
        List<BinaryFile> newFiles = rdata.getFileList("files");
        for (BinaryFile f : newFiles) {
            if (f.isImage()){
                DefectImageData image = new DefectImageData();
                image.setCreateValues(this, rdata);
                if (!image.createFromBinaryFile(f, image.getMaxWidth(), image.getMaxHeight(), image.getMaxPreviewWidth(),image.getMaxPreviewHeight(), false))
                    continue;
                image.setChangerId(rdata.getUserId());
                getFiles().add(image);
            }
            else {
                DefectDocumentData document = new DefectDocumentData();
                document.setCreateValues(this, rdata);
                document.createFromBinaryFile(f);
                document.setChangerId(rdata.getUserId());
                getFiles().add(document);
            }
        }
    }

    public void readApiRequestData(RequestData rdata) {
        setCreatorId(rdata.getInt("creatorId"));
        setDescription(rdata.getString("description"));
        setAssignedId(rdata.getInt("assignedId"));
        setLot(rdata.getString("lot"));
        setPositionX(rdata.getInt("positionX"));
        setPositionY(rdata.getInt("positionY"));
        setPositionComment(rdata.getString("positionComment"));
        setState(rdata.getString("state"));
        setCreationDate(DateUtil.asLocalDateTime(rdata.getLong("creationDate")));
        setDueDate1(DateUtil.asLocalDate(rdata.getLong("dueDate")));
        setLocationId(rdata.getInt("locationId"));
    }


    @SuppressWarnings("unchecked")
    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("creationDate", DateUtil.asMillis(getCreationDate()));
        json.put("creatorId", getCreatorId());
        json.put("creatorName", getCreatorName());
        json.put("displayId",getDisplayId());
        json.put("description",getDescription());
        json.put("assignedId",getAssignedId());
        json.put("assignedName",getAssignedName());
        json.put("lot",getLot());
        json.put("planId",getPlanId());
        json.put("positionX",getPositionX());
        json.put("positionY",getPositionY());
        json.put("positionComment",getPositionComment());
        json.put("state", getState());
        json.put("dueDate", DateUtil.asMillis(getDueDate()));
        return json;
    }

}
