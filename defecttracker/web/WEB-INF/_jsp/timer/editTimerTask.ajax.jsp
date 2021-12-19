<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.timer.TimerInterval" %>
<%@ page import="de.elbe5.timer.TimerTaskData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
    if ((data == null))
        throw new AssertionError();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_taskSettings",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="/ctrl/timer/saveTimerTask" name="taskform" ajax="true">
            <input type="hidden" name="timerName" value="<%=data.getName()%>"/>
            <div class="modal-body">
                <form:formerror/>
                <form:line label="_name"><%=$H(data.getName())%>
                </form:line>
                <form:line label="_displayName"><%=$H(data.getDisplayName())%>
                </form:line>
                <form:line label="_intervalType" padded="true" required="true">
                    <form:radio name="interval" value="<%=TimerInterval.CONTINOUS.name()%>" checked="<%=data.getInterval() == TimerInterval.CONTINOUS%>"><%=$SH("_continous",locale)%>
                    </form:radio><br/>
                    <form:radio name="interval" value="<%=TimerInterval.MONTH.name()%>" checked="<%=data.getInterval() == TimerInterval.MONTH%>"><%=$SH("_monthly",locale)%>
                    </form:radio><br/>
                    <form:radio name="interval" value="<%=TimerInterval.DAY.name()%>" checked="<%=data.getInterval() == TimerInterval.DAY%>"><%=$SH("_daily",locale)%>
                    </form:radio><br/>
                    <form:radio name="interval" value="<%=TimerInterval.HOUR.name()%>" checked="<%=data.getInterval() == TimerInterval.HOUR%>"><%=$SH("_everyHour",locale)%>
                    </form:radio>
                </form:line>
                <form:text name="day" label="_day" required="true" value="<%=$I(data.getDay())%>"/>
                <form:text name="hour" label="_hour" required="true" value="<%=$I(data.getHour())%>"/>
                <form:text name="minute" label="_minute" required="true" value="<%=$I(data.getMinute())%>"/>
                <form:line label="_active" padded="true"><form:check name="active" value="true" checked="<%=data.isActive()%>"/></form:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=$SH("_save",locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>

