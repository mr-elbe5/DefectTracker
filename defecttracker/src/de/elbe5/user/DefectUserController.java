/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.project.ProjectBean;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.view.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DefectUserController extends UserController {

    @Override
    public IView login(SessionRequestData rdata) {
        checkRights(rdata.isPostback());
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            rdata.setMessage(Strings.string("_notComplete",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        UserData data = UserBean.getInstance().loginUser(login, pwd);
        if (data == null) {
            Log.info("bad login of "+login);
            rdata.setMessage(Strings.string("_badLogin",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        ViewFilter filter = ViewFilter.getFilter(rdata);
        boolean isEditor = data.hasSystemRight(SystemZone.CONTENTEDIT);
        filter.setEditor(isEditor);
        filter.setCurrentUserId(data.getId());
        Map<String,String> cookieValues = rdata.readLoginCookies();
        if (cookieValues.containsKey("showClosed"))
            filter.setShowClosed(Boolean.parseBoolean(cookieValues.get("showClosed")));
        List<Integer> projectIds= ProjectBean.getInstance().getUserProjectIds(data.getId(),isEditor);
        filter.getOwnProjectIds().clear();
        filter.getOwnProjectIds().addAll(projectIds);
        if (projectIds.isEmpty() && !isEditor){
            rdata.setMessage(Strings.string("_noProject",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        rdata.setSessionUser(data);
        String language=rdata.getString("language");
        if (!language.isEmpty())
            rdata.setSessionLocale(new Locale(language));
        if (projectIds.size()==1){
            filter.setProjectId(projectIds.get(0));
        } else if (cookieValues.containsKey("projectId")){
            int id=Integer.parseInt(cookieValues.get("projectId"));
            if (projectIds.contains(id))
                filter.setProjectId(id);
        }
        return new UrlView("/ctrl/project/showHome");
    }

}
