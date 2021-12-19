/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.timer;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.cache.Strings;
import de.elbe5.request.SessionRequestData;

import java.time.LocalDateTime;

public abstract class TimerTaskData extends BaseData implements Cloneable {


    protected String displayName = "";
    protected TimerInterval interval = TimerInterval.CONTINOUS;
    protected int day = 0;
    protected int hour = 0;
    protected int minute = 0;
    protected LocalDateTime lastExecution = null;
    protected LocalDateTime nextExecution = null;
    protected boolean active = false;

    public TimerTaskData(){
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public abstract String getName();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TimerInterval getInterval() {
        return interval;
    }

    public void setInterval(TimerInterval interval) {
        this.interval = interval;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public LocalDateTime getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(LocalDateTime lastExecution) {
        if (lastExecution == null) {
            this.lastExecution = TimerBean.getInstance().getServerTime();
        } else {
            this.lastExecution = lastExecution;
        }
    }

    protected LocalDateTime computeNextExecution(LocalDateTime now) {
        LocalDateTime next;
        if (lastExecution == null)
            lastExecution = LocalDateTime.now();
        switch (interval) {
            case CONTINOUS: {
                next = lastExecution.plusDays(getDay());
                next = next.plusHours(getHour());
                next = next.plusMinutes(getMinute());
                return next;
            }
            case MONTH: {
                next = now.withDayOfMonth(getDay());
                next = next.withHour(getHour());
                next = next.withMinute(getMinute());
                next = next.withSecond(0);
                if (next.isAfter(now)) {
                    next = next.minusMonths(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next = next.plusMonths(1);
                }
                return next;
            }
            case DAY: {
                next = now.withHour(getHour());
                next = next.withMinute(getMinute());
                next = next.withSecond(0);
                if (next.isAfter(now)) {
                    next = next.minusDays(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next = next.plusDays(1);
                }
                return next;
            }
            case HOUR: {
                next = now.withMinute(getMinute());
                next = next.withSecond(0);
                if (next.isAfter(now)) {
                    next = next.minusHours(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next = next.plusHours(1);
                }
                return next;
            }
        }
        return LocalDateTime.now();
    }

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(LocalDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean execute(LocalDateTime now) {
        if (execute(nextExecution, now)) {
            setLastExecution(nextExecution);
            setNextExecution(computeNextExecution(now));
        }
        return true;
    }

    public void readSettingsRequestData(SessionRequestData rdata) {
        setInterval(TimerInterval.valueOf(rdata.getString("interval")));
        setDay(rdata.getInt("day"));
        setHour(rdata.getInt("hour"));
        setMinute(rdata.getInt("minute"));
        setActive(rdata.getBoolean("active"));
        if (interval != TimerInterval.CONTINOUS && (day == 0 || (hour < 0 || hour >= 24) || (minute < 0 || minute >= 60))) {
            rdata.addFormError(Strings.string("_timerSettingsError",rdata.getLocale()));
            rdata.addFormField("interval");
            rdata.addFormField("day");
            rdata.addFormField("hour");
            rdata.addFormField("minute");
        }
    }

    public abstract boolean execute(LocalDateTime executionTime, LocalDateTime checkTime);

}
