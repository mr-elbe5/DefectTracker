/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import javax.servlet.ServletContext;
import java.io.File;

public class ApplicationPath {

    private static String appName = "";
    private static String appPath = "";
    private static String appROOTPath = "";
    private static String appWEBINFPath = "";

    public static String getAppName() {
        return appName;
    }

    public static String getAppPath() {
        return appPath;
    }

    public static String getAppROOTPath() {
        return appROOTPath;
    }

    public static String getAppWEBINFPath() {
        return appWEBINFPath;
    }

    public static void initializePath(File appDir, File appROOTDir) {
        if (appDir == null || appROOTDir == null) {
            return;
        }
        appPath = appDir.getAbsolutePath().replace('\\', '/');
        appName = appPath.substring(appPath.lastIndexOf('/') + 1);
        System.out.println("application name is: " + getAppName());
        System.out.println("application path is: " + getAppPath());
        appROOTPath = appROOTDir.getAbsolutePath().replace('\\', '/');
        appWEBINFPath = appROOTPath + "/WEB-INF";
    }

    public static File getCatalinaBaseDir() {
        return new File(System.getProperty("catalina.base")).getAbsoluteFile();
    }

    public static File getCatalinaConfDir() {
        return new File(getCatalinaBaseDir(), "conf");
    }

    public static File getCatalinaAppDir(ServletContext context) {
        return getCatalinaAppROOTDir(context).getParentFile();
    }

    public static File getCatalinaAppROOTDir(ServletContext context) {
        return new File(context.getRealPath("/"));
    }
}
