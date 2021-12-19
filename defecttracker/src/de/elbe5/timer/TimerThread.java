/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.timer;

import de.elbe5.base.log.Log;
import de.elbe5.base.thread.BaseThread;

import java.time.LocalDateTime;

public class TimerThread extends BaseThread {

    protected int interval;

    public TimerThread(int interval) {
        super("CMSTaskTimer");
        this.interval = interval * 1000;
    }

    @Override
    public void run() {
        Log.log("timer started");
        while (running) {
            try {
                sleep(interval);
                checkTasks();
            } catch (InterruptedException e) {
                break;
            }
        }
        Log.log("timer stopped");
        running = false;
    }

    protected void checkTasks() {
        LocalDateTime now = TimerBean.getInstance().getServerTime();
        for (TimerTaskData task : Timer.getInstance().getTasks().values()) {
            try {
                if (task.isActive()) {
                    checkTask(task, now);
                }
            } catch (Exception e) {
                Log.error("could not execute timer task", e);
            }
        }
    }

    protected void checkTask(TimerTaskData task, LocalDateTime now) {
        if (now == null) {
            return;
        }
        LocalDateTime next = task.getNextExecution();
        if (now.isAfter(next)) {
            task.execute(now);
        }
    }
}
