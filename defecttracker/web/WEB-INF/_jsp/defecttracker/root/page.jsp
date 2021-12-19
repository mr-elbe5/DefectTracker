
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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ViewFilter filter = ViewFilter.getFilter(rdata);
%>
<form:message/>
<section class="contentSection" id="content">
    <div class="projectSelector">
        <h1><%=$SH("_selectProject",locale)%></h1>
        <% for (int projectId : filter.getOwnProjectIds()){
            ProjectData project= ContentCache.getContent(projectId,ProjectData.class);
            if (project==null)
                continue;%>
        <div>
            <a class="btn btn-outline-primary" href="/ctrl/project/selectProject/<%=project.getId()%>"><%=$H(project.getName())%></a>
        </div>
        <%}%>
    </div>
</section>
