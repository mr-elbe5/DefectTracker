/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.timer;

import de.elbe5.application.AdminController;
import de.elbe5.base.cache.Strings;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.CloseDialogView;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

public class TimerController extends Controller {

    public static final String KEY = "timer";

    private static TimerController instance = null;

    public static void setInstance(TimerController instance) {
        TimerController.instance = instance;
    }

    public static TimerController getInstance() {
        return instance;
    }

    public static void register(TimerController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openEditTimerTask(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        String name = rdata.getString("timerName");
        TimerTaskData task = Timer.getInstance().getTaskCopy(name);
        rdata.setSessionObject("timerTaskData", task);
        return showEditTimerTask();
    }

    public IView saveTimerTask(SessionRequestData rdata) {
        checkRights(rdata.hasSystemRight(SystemZone.APPLICATION));
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        assert(data!=null);
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditTimerTask();
        }
        TimerBean ts = TimerBean.getInstance();
        ts.updateTaskData(data);
        Timer.getInstance().loadTask(data.getName());
        rdata.setMessage(Strings.string("_taskSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogView("/ctrl/admin/openSystemAdministration");
    }

    private IView showEditTimerTask() {
        return new UrlView("/WEB-INF/_jsp/timer/editTimerTask.ajax.jsp");
    }

}
