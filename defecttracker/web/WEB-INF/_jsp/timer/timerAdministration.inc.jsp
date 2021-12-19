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
<%@ page import="de.elbe5.timer.Timer" %>
<%@ page import="de.elbe5.timer.TimerTaskData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    Map<String, TimerTaskData> tasks = null;
    try {
        Timer timerCache = Timer.getInstance();
        tasks = timerCache.getTasks();
    } catch (Exception ignore) {
    }
%>
<li class="open">
    <%=$SH("_timers",locale)%>
    <ul>
        <%
            if (tasks != null) {
                for (TimerTaskData task : tasks.values()) {
        %>
        <li>
            <span><%=$H(task.getDisplayName())%></span>
            <div class="icons">
                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/timer/openEditTimerTask?timerName=<%=task.getName()%>');" title="<%=$SH("_edit",locale)%>"></a>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
