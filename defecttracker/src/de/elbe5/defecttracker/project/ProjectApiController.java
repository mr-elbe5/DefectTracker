/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.project;

import de.elbe5.application.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.BaseApiController;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.defect.DefectCommentData;
import de.elbe5.defecttracker.defect.DefectCommentImageData;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.defecttracker.defect.DefectImageData;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.defecttracker.location.PlanImageData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;
import de.elbe5.view.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Locale;

public class ProjectApiController extends BaseApiController {

    public static final String KEY = "project";

    private static ProjectApiController instance = null;

    public static void setInstance(ProjectApiController instance) {
        ProjectApiController.instance = instance;
    }

    public static ProjectApiController getInstance() {
        return instance;
    }

    public static void register(ProjectApiController controller){
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IApiView getProjects(ApiRequestData rdata) {
        UserData user = rdata.getLoginUser();
        if (user==null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        JSONObject json = getProjectsJson(user);
        return new JsonView(json.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private JSONObject getProjectsJson(UserData user) {
        boolean isEditor = user.hasSystemRight(SystemZone.CONTENTEDIT);
        ViewFilter filter = new ViewFilter();
        filter.setEditor(isEditor);
        filter.setCurrentUserId(user.getId());
        filter.setShowClosed(false);
        List<Integer> projectIds= ProjectBean.getInstance().getUserProjectIds(user.getId(),isEditor);
        //Log.log("found projectIds: " + projectIds.size());
        Locale locale= Configuration.getDefaultLocale();
        JSONObject json = new JSONObject();
        JSONArray jsProjects=new JSONArray();
        json.put("projects",jsProjects);
        for (int projectId : projectIds){
            ProjectData project = ContentCache.getContent(projectId,ProjectData.class);
            //Log.info("project is: " + (project == null ? "null" : project.getName()));
            assert(project != null);
            if (!project.isActive()){
                Log.warn("skipping inactive project: " + project.getName());
                continue;
            }
            JSONObject jsProject = project.getJson(locale);
            GroupData group = GroupBean.getInstance().getGroup(project.getGroupId());
            JSONArray jsUsers=new JSONArray();
            for (int uid : group.getUserIds()) {
                UserData ud = UserCache.getUser(uid);
                JSONObject jsUser = ud.getJsonShort(locale);
                jsUsers.add(jsUser);
            }
            jsProject.put("users",jsUsers);
            //Log.info("found project users: " + jsUsers.size());
            jsProjects.add(jsProject);
            JSONArray jsLocations=new JSONArray();
            jsProject.put("locations", jsLocations);
            for (LocationData location : project.getChildren(LocationData.class)) {
                //Log.info("location is: " + (location == null ? "null" : location.getName()));
                if (!location.isActive()){
                    Log.warn("skipping inactive location: " + location.getName());
                    continue;
                }
                JSONObject jsLocation = location.getJson(locale);
                jsLocations.add(jsLocation);
                PlanImageData plan = location.getPlan();
                if (plan != null) {
                    JSONObject jsPlan = plan.getJson(locale);
                    jsLocation.put("plan", jsPlan);
                }
                JSONArray jsDefects = new JSONArray();
                jsLocation.put("defects", jsDefects);
                for (DefectData defect : location.getChildren(DefectData.class)) {
                    //Log.info("defect is: " + (defect == null ? "null" : defect.getName()));
                    if (!defect.isActive()){
                        Log.warn("skipping inactive defect: " + defect.getDisplayId());
                        continue;
                    }
                    JSONObject jsDefect = defect.getJson();
                    jsDefects.add(jsDefect);
                    JSONArray jsImages = new JSONArray();
                    jsDefect.put("images", jsImages);
                    for (DefectImageData image : defect.getFiles(DefectImageData.class)) {
                        JSONObject jsImage = image.getJson(locale);
                        jsImages.add(jsImage);
                    }
                    List<DefectCommentImageData> commentImages = defect.getFiles(DefectCommentImageData.class);
                    JSONArray jsComments = new JSONArray();
                    jsDefect.put("comments", jsComments);
                    for (DefectCommentData comment : defect.getComments()) {
                        JSONObject jsComment = comment.getJson();
                        jsComments.add(jsComment);
                        JSONArray jsCommentImages = new JSONArray();
                        jsComment.put("images", jsCommentImages);
                        for (DefectCommentImageData image : commentImages) {
                            if (image.getCommentId() == comment.getId()) {
                                JSONObject jsImage = image.getJson(locale);
                                jsCommentImages.add(jsImage);
                            }
                        }
                    }
                }
            }
        }
        return json;
    }

}
