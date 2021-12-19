/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.defecttracker.defect.*;
import de.elbe5.defecttracker.location.*;
import de.elbe5.defecttracker.project.ProjectApiController;
import de.elbe5.defecttracker.root.RootPageData;
import de.elbe5.defecttracker.project.ProjectBean;
import de.elbe5.defecttracker.project.ProjectController;
import de.elbe5.defecttracker.project.ProjectData;
import de.elbe5.content.*;
import de.elbe5.database.DbConnector;
import de.elbe5.file.*;
import de.elbe5.group.GroupController;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.servlet.InitServlet;
import de.elbe5.timer.CleanupTaskData;
import de.elbe5.timer.HeartbeatTaskData;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerController;
import de.elbe5.user.DefectUserController;
import de.elbe5.user.UserApiController;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class DefectTrackerInitServlet extends InitServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        System.out.println("initializing Defect Tracker Application...");
        ServletContext context=servletConfig.getServletContext();
        ApplicationPath.initializePath(ApplicationPath.getCatalinaAppDir(context), ApplicationPath.getCatalinaAppROOTDir(context));
        Configuration.setConfigs(context);
        Log.initLog(ApplicationPath.getAppName());
        if (!DbConnector.getInstance().initialize("jdbc/defecttracker"))
            return;
        Configuration.setAppTitle("Defect Tracker");
        Strings.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/webserver-strings.csv");
        Strings.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/application-strings.csv");
        if (Strings.hasLocale(Configuration.getDefaultLocale())) {
            Strings.DEFAULT_LOCALE = Configuration.getDefaultLocale();
        }
        AdminController.register(new AdminController());
        ContentController.register(new ContentController());
        DocumentController.register(new DocumentController());
        ImageController.register(new ImageController());
        GroupController.register(new GroupController());
        TimerController.register(new TimerController());
        UserController.register(new DefectUserController());
        ProjectController.register(new ProjectController());
        DefectController.register(new DefectController());
        LocationController.register(new LocationController());
        UserApiController.register(new UserApiController());
        DocumentApiController.register(new DocumentApiController());
        ImageApiController.register(new ImageApiController());
        ProjectApiController.register(new ProjectApiController());
        LocationApiController.register(new LocationApiController());
        DefectApiController.register(new DefectApiController());
        ContentFactory.addClassInfo(ContentData.class, ContentBean.getInstance());
        FileFactory.addDocumentClassInfo(DocumentData.class, null);
        FileFactory.addImageClassInfo(ImageData.class, ImageBean.getInstance());
        FileFactory.getDefaultDocumentTypes().clear();
        FileFactory.getDefaultImageTypes().clear();
        ContentFactory.getDefaultTypes().clear();
        ContentFactory.addClassInfo(RootPageData.class, null);
        ContentFactory.addClassInfo(ProjectData.class, ProjectBean.getInstance());
        ContentFactory.addClassInfo(LocationData.class, LocationBean.getInstance());
        ContentFactory.addClassInfo(DefectData.class, DefectBean.getInstance());
        FileFactory.addImageClassInfo(PlanImageData.class, ImageBean.getInstance());
        FileFactory.addDocumentClassInfo(DefectDocumentData.class, FileBean.getInstance());
        FileFactory.addImageClassInfo(DefectImageData.class, ImageBean.getInstance());
        FileFactory.addDocumentClassInfo(DefectCommentDocumentData.class, DefectCommentDocumentBean.getInstance());
        FileFactory.addImageClassInfo(DefectCommentImageData.class, DefectCommentImageBean.getInstance());
        ContentCache.load();
        UserCache.load();
        Timer.getInstance().registerTimerTask(new HeartbeatTaskData());
        Timer.getInstance().registerTimerTask(new CleanupTaskData());
        Log.log("load tasks");
        Timer.getInstance().loadTasks();
        Timer.getInstance().startThread();
        Log.log("Defect tracker initialized");
        //generatePassword();
    }

}
