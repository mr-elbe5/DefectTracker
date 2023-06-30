/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.project;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.DefectBaseController;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.request.SessionRequestData;
import de.elbe5.user.UserBean;
import de.elbe5.view.*;

public class ProjectController extends DefectBaseController {

    public static int COOKIE_EXPIRATION_DAYS = 90;

    public static final String KEY = "project";

    private static ProjectController instance = null;

    public static void setInstance(ProjectController instance) {
        ProjectController.instance = instance;
    }

    public static ProjectController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openWatchFilter(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        int contentId=rdata.getId();
        return new UrlView("/WEB-INF/_jsp/defecttracker/project/watchFilter.ajax.jsp");
    }

    public IView setWatchFilter(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        ViewFilter filter = ViewFilter.getSessionFilter(rdata);
        filter.setWatchedIds(rdata.getIntegerList("watchedIds"));
        return new CloseDialogView("/ctrl/content/show/" + filter.getProjectId());
    }

    public IView updateWatchedUsers(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return new UrlView("/WEB-INF/_jsp/defecttracker/project/projectUsers.ajax.jsp");
    }

    public IView openFilterSettings(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return new UrlView("/WEB-INF/_jsp/defecttracker/project/filterSettings.ajax.jsp");
    }

    public IView setFilterSettings(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        rdata.getSessionUser().setShowClosed(rdata.getBoolean("showClosed"));
        rdata.getSessionUser().setApiCurrentProjectOnly(rdata.getBoolean("apiCurrentProjectOnly"));
        UserBean.getInstance().changeFilterSettings(rdata.getUserId(), rdata.getSessionUser());
        return new CloseDialogView("/ctrl/content/show/" + rdata.getSessionUser().getCurrentProjectId());
    }

    public IView selectProject(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        int projectId=rdata.getId();
        ViewFilter filter = ViewFilter.getSessionFilter(rdata);
        filter.setProjectId(projectId);
        rdata.getSessionUser().setCurrentProjectId(projectId);
        UserBean.getInstance().changeCurrentProject(rdata.getUserId(), projectId);
        ProjectData data= ContentCache.getContent(projectId,ProjectData.class);
        return new ContentView(data);
    }

    public IView getExcel(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ProjectData project=ContentCache.getContent(contentId,ProjectData.class);
        assert(project!=null);
        BinaryFile file = ProjectXslxBean.getInstance().getProjectExcel(project);
        assert(file!=null);
        BinaryFileView view=new BinaryFileView(file);
        view.setForceDownload(true);
        return view;
    }

    public IView getReport(SessionRequestData rdata) {
        boolean includeComments = rdata.getBoolean("includeComments");
        int contentId = rdata.getId();
        BinaryFile file = ProjectPdfBean.getInstance().getProjectReport(contentId, rdata, includeComments);
        assert(file!=null);
        BinaryFileView view=new BinaryFileView(file);
        view.setForceDownload(true);
        return view;
    }

    public IView sort(SessionRequestData rdata) {
        int sortType = rdata.getInt("sortType");
        ViewFilter filter = ViewFilter.getSessionFilter(rdata);
        filter.setSortType(sortType);
        return show(rdata);
    }

}
