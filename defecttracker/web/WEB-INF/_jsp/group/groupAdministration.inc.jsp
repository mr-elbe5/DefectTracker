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
<%@ page import="de.elbe5.group.GroupBean" %>
<%@ page import="de.elbe5.group.GroupData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    List<GroupData> groups = null;
    try {
        groups = GroupBean.getInstance().getAllGroups();
    } catch (Exception ignore) {
    }
    int groupId = rdata.getInt("groupId");
%><!--groups-->
<li class="open">
    <span><%=$SH("_groups",locale)%></span>
    <div class="icons">
        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/group/openCreateGroup');" title="<%=$SH("_new",locale)%>"> </a>
    </div>
    <ul>
        <%
            if (groups != null) {
                for (GroupData group : groups) {
        %>
        <li class="<%=groupId==group.getId() ? "open" : ""%>">
            <span><%=$H(group.getName())%></span>
            <div class="icons">
                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/group/openEditGroup/<%=group.getId()%>');" title="<%=$SH("_edit",locale)%>"></a>
                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/group/deleteGroup/<%=group.getId()%>');" title="<%=$SH("_delete",locale)%>"></a>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>


