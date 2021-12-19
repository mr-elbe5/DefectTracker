/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.group;

import de.elbe5.application.AdminController;
import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BaseData;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.*;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.UserCache;
import de.elbe5.view.CloseDialogView;
import de.elbe5.view.IView;
import de.elbe5.view.RedirectView;
import de.elbe5.view.UrlView;

import java.util.Locale;

public class GroupController extends Controller {

    public static final String KEY = "group";

    private static GroupController instance = null;

    public static void setInstance(GroupController instance) {
        GroupController.instance = instance;
    }

    public static GroupController getInstance() {
        return instance;
    }

    public static void register(GroupController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openEditGroup(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int groupId = rdata.getId();
        GroupData data = GroupBean.getInstance().getGroup(groupId);
        rdata.setSessionObject("groupData", data);
        return showEditGroup();
    }

    public IView openCreateGroup(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        GroupData data = new GroupData();
        data.setNew(true);
        data.setId(GroupBean.getInstance().getNextId());
        rdata.setSessionObject("groupData", data);
        return showEditGroup();
    }

    public IView saveGroup(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        GroupData data = (GroupData) rdata.getSessionObject("groupData");
        assert(data!=null);
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditGroup();
        }
        GroupBean.getInstance().saveGroup(data);
        UserCache.setDirty();
        rdata.setMessage(Strings.string("_groupSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogView("/ctrl/admin/openPersonAdministration?groupId=" + data.getId());
    }

    public IView deleteGroup(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.USER));
        int id = rdata.getId();
        if (id < BaseData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return new UrlView("/ctrl/admin/openPersonAdministration");
        }
        GroupBean.getInstance().deleteGroup(id);
        UserCache.setDirty();
        rdata.setMessage(Strings.string("_groupDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new UrlView("/ctrl/admin/openPersonAdministration");
    }

    protected IView showEditGroup() {
        return new UrlView("/WEB-INF/_jsp/group/editGroup.ajax.jsp");
    }
}
