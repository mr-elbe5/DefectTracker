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
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.group.GroupData" %>
<%@ page import="de.elbe5.group.GroupBean" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="de.elbe5.user.UserCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    int projectId=rdata.getInt("projectId");
    ViewFilter filter= ViewFilter.getFilter(rdata);
    GroupData group=null;
    ProjectData project=ContentCache.getContent(projectId, ProjectData.class);
    if (project!=null)
        group= GroupBean.getInstance().getGroup(project.getGroupId());
%>
                    <option value="0" <%=filter.getAssignedId()==0 ? "selected" : ""%>><%=$SH("_all", locale)%>
                    </option>
                    <% if (group!=null){
                        for (int userId : group.getUserIds()){
                            UserData user= UserCache.getUser(userId);
                            if (user==null)
                                continue;%>
<option value="<%=user.getId()%>" <%=filter.getAssignedId()==user.getId() ? "selected" : ""%>><%=$H(user.getName())%>
</option>
<%
                        }
                    }%>

