/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.content.ContentCache;
import de.elbe5.file.BinaryFileCache;
import de.elbe5.base.cache.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.database.DbConnector;
import de.elbe5.servlet.CmsAuthorizationException;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.UserCache;
import de.elbe5.view.CloseDialogView;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

import java.io.File;
import java.io.IOException;

public class AdminController extends Controller {

    public static final String KEY = "admin";

    private static AdminController instance = null;

    public static void setInstance(AdminController instance) {
        AdminController.instance = instance;
    }

    public static AdminController getInstance() {
        return instance;
    }

    public static void register(AdminController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openAdministration(SessionRequestData rdata){
        if (rdata.hasSystemRight(SystemZone.CONTENTEDIT))
            return openContentAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION))
            return openSystemAdministration(rdata);
        if (rdata.hasSystemRight(SystemZone.USER))
            return openPersonAdministration(rdata);
        throw new CmsAuthorizationException();
    }

    public IView openSystemAdministration(SessionRequestData rdata) {
        checkRights(rdata.hasAnySystemRight());
        return showSystemAdministration(rdata);
    }

    public IView openPersonAdministration(SessionRequestData rdata) {
        checkRights(rdata.hasAnySystemRight());
        return showPersonAdministration(rdata);
    }

    public IView openContentAdministration(SessionRequestData rdata) {
        checkRights(rdata.hasAnyContentRight());
        return showContentAdministration(rdata);
    }

    public IView restart(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        String path = ApplicationPath.getAppROOTPath() + "/WEB-INF/web.xml";
        File f = new File(path);
        try {
            FileUtil.touch(f);
        } catch (IOException e) {
            Log.error("could not touch file " + path, e);
        }
        rdata.setMessage(Strings.string("_restartHint",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IView reloadContentCache(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        ContentCache.setDirty();
        ContentCache.checkDirty();
        rdata.setMessage(Strings.string("_cacheReloaded",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IView reloadUserCache(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        UserCache.setDirty();
        UserCache.checkDirty();
        rdata.setMessage(Strings.string("_cacheReloaded",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IView clearFileCache(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        BinaryFileCache.setDirty();
        rdata.setMessage(Strings.string("_cacheCleared",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return openSystemAdministration(rdata);
    }

    public IView toggleInactiveContent(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.CONTENTEDIT));
        Configuration.setShowInactiveContent(!Configuration.isShowInactiveContent());
        return openContentAdministration(rdata);
    }

    private IView showEditConfiguration() {
        return new UrlView("/WEB-INF/_jsp/administration/editConfiguration.ajax.jsp");
    }

}
