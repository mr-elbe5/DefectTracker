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
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    UserData user = (UserData) rdata.getSessionObject("userData");
    assert user != null;
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    String label;
    String url = "/ctrl/user/saveUser/" + user.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editUser",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="userform" multi="true" ajax="true">
            <div class="modal-body">
                <form:formerror/>
                <h3><%=$SH("_settings",locale)%>
                </h3>
                <form:line label="_id"><%=$I(user.getId())%>
                </form:line>
                <form:text name="login" label="_login" required="true" value="<%=$H(user.getLogin())%>"/>
                <form:password name="password" label="_password"/>
                <form:text name="title" label="_title" value="<%=$H(user.getTitle())%>"/>
                <form:text name="firstName" label="_firstName" value="<%=$H(user.getFirstName())%>"/>
                <form:text name="lastName" label="_lastName" required="true" value="<%=$H(user.getLastName())%>"/>
                <form:textarea name="notes" label="_notes" height="5rem"><%=$H(user.getNotes())%>
                </form:textarea>
                <form:file name="portrait" label="_portrait"><% if (user.hasPortrait()) {%><img src="/ctrl/user/showPortrait/<%=user.getId()%>" alt="<%=$H(user.getName())%>"/> <%}%>
                </form:file>
                <h3><%=$SH("_address",locale)%>
                </h3>
                <form:text name="street" label="_street" value="<%=$H(user.getStreet())%>"/>
                <form:text name="zipCode" label="_zipCode" value="<%=$H(user.getZipCode())%>"/>
                <form:text name="city" label="_city" value="<%=$H(user.getCity())%>"/>
                <form:text name="country" label="_country" value="<%=$H(user.getCountry())%>"/>
                <h3><%=$SH("_contact",locale)%>
                </h3>
                <form:text name="email" label="_email" required="true" value="<%=$H(user.getEmail())%>"/>
                <form:text name="phone" label="_phone" value="<%=$H(user.getPhone())%>"/>
                <form:text name="fax" label="_fax" value="<%=$H(user.getFax())%>"/>
                <form:text name="mobile" label="_mobile" value="<%=$H(user.getMobile())%>"/>
                <h3><%=$SH("_groups",locale)%>
                </h3>
                <form:line label="_group"><%=$SH("_inGroup",locale)%>
                </form:line>
                <% for (GroupData gdata : groups) {%><%
                label = gdata.getName();%>
                <form:line label="<%=label%>" padded="true">
                    <form:check name="groupIds" value="<%=$I(gdata.getId())%>" checked="<%=user.getGroupIds().contains(gdata.getId())%>"/>
                </form:line>
                <%}%>
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
