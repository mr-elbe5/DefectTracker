/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.time.*;

public class CalendarDateTime {

    public enum Scope {
        DAY, WEEK, MONTH
    }

    protected LocalDateTime current;
    protected Scope scope;
    protected LocalDate today;
    protected LocalDate firstDayInScope;
    protected LocalDate lastDayInScope;
    protected LocalDate firstVisibleDay;
    protected LocalDate lastVisibleDay;
    protected int days;
    protected int visibleDays;

    public CalendarDateTime(Scope scope) {
        current = LocalDateTime.now();
        this.scope = scope;
        setBaseValues();
    }

    public CalendarDateTime(LocalDateTime now, Scope scope) {
        current = now;
        this.scope = scope;
        setBaseValues();
    }

    private void setBaseValues() {
        today = current.toLocalDate();
        switch (scope) {
            case DAY:
                firstDayInScope = today;
                lastDayInScope = today;
                firstVisibleDay = firstDayInScope;
                lastVisibleDay = lastDayInScope;
                days = 1;
                visibleDays = 1;
                break;
            case WEEK:
                firstDayInScope = today.minusDays(today.getDayOfWeek().getValue() - 1);
                lastDayInScope = firstDayInScope.plusDays(6);
                firstVisibleDay = firstDayInScope;
                lastVisibleDay = lastDayInScope;
                days = 7;
                visibleDays = 7;
                break;
            case MONTH:
                Month currentMonth = today.getMonth();
                int lengthOfMonth = currentMonth.minLength();
                if (currentMonth.maxLength() != lengthOfMonth) {
                    if (today.isLeapYear() && currentMonth.equals(Month.FEBRUARY))
                        lengthOfMonth = currentMonth.maxLength();
                }
                days = lengthOfMonth;
                visibleDays = lengthOfMonth;
                firstDayInScope = today.minusDays(today.getDayOfMonth() - 1);
                int diff = firstDayInScope.getDayOfWeek().getValue() - 1;
                visibleDays += diff;
                firstVisibleDay = firstDayInScope.minusDays(diff);
                lastDayInScope = firstDayInScope.plusDays(lengthOfMonth - 1);
                diff = 7 - lastDayInScope.getDayOfWeek().getValue();
                visibleDays += diff;
                lastVisibleDay = lastDayInScope.plusDays(diff);
                break;
        }
    }


    public String toString() {
        return current.toString() + "\ntoday:" + today + "\nscope:" + scope + "\ncurrentMonth:" + getCurrentMonth() + "\nfirstVisibleDay:" + firstVisibleDay + "\nfirstDayInScope:" + firstDayInScope + "\nlastDayInScope:" + lastDayInScope + "\nlastVisibleDay:" + lastVisibleDay + "\ndays:" + days + "\nvisibleDays:" + visibleDays;
    }

    public LocalDateTime getCurrent() {
        return current;
    }

    public void setCurrent(LocalDateTime current) {
        this.current = current;
    }

    public LocalDate getToday() {
        return today;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
        setBaseValues();
    }

    public Month getCurrentMonth() {
        return today.getMonth();
    }

    public LocalDate getFirstDayInScope() {
        return firstDayInScope;
    }

    public LocalDate getLastDayInScope() {
        return lastDayInScope;
    }

    public LocalDate getFirstVisibleDay() {
        return firstVisibleDay;
    }

    public LocalDate getLastVisibleDay() {
        return lastVisibleDay;
    }

    public int getDays() {
        return days;
    }

    public int getVisibleDays() {
        return visibleDays;
    }

}
