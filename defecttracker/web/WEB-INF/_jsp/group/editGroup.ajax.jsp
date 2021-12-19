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
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.group.GroupData" %>
<%@ page import="de.elbe5.user.UserBean" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    GroupData group = rdata.getSessionObject("groupData",GroupData.class);
    assert group != null;
    List<UserData> users = UserBean.getInstance().getAllUsers();
    String name, label;
    String url = "/ctrl/group/saveGroup/" + group.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editGroup",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="groupform" ajax="true">
            <div class="modal-body">
                <form:formerror/>
                <h3><%=$SH("_settings",locale)%>
                </h3>
                <form:line label="_id"><%=$I(group.getId())%>
                </form:line>
                <form:text name="name" label="_name" required="true" value="<%=$H(group.getName())%>"/>
                <form:textarea name="notes" label="_notes" height="5rem"><%=$H(group.getNotes())%>
                </form:textarea>
                <form:line label="_globalRights" padded="true">
                <%for (SystemZone zone : SystemZone.values()) {
                    name="zoneright_"+zone.name();
                %>
                    <form:check name="<%=name%>" value="true" checked="<%=group.hasSystemRight(zone)%>"><%=$H(zone.name())%>
                    </form:check><br/>
                    <%}%>
                </form:line>
                <h3><%=$SH("_users",locale)%>
                </h3>
                <form:line label="_user"><%=$SH("_inGroup",locale)%>
                </form:line>
                <% for (UserData udata : users) {%><%
                label = udata.getName();%>
                <form:line label="<%=label%>" padded="true">
                    <form:check name="userIds" value="<%=$I(udata.getId())%>" checked="<%=group.getUserIds().contains(udata.getId())%>"/>
                </form:line>
                <%}%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save",locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>


