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
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.group.GroupData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.group.GroupBean" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentSessionContent();
    assert (contentData != null);
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    String label, name;
    String url = "/ctrl/content/saveRights/" + contentData.getId();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editGroupRights", locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="pageform" ajax="true">
            <div class="modal-body">
                <form:formerror/>
                <%for (GroupData group : groups) {
                if (group.getId() <= GroupData.ID_MAX_FINAL)
                    continue;
                    {
                    label = StringUtil.toHtml(group.getName());
                    name = "groupright_" + group.getId();%>
                    <form:line label="<%=label%>" padded="true">
                        <form:radio name="<%=name%>" value="" checked="<%=!contentData.hasAnyGroupRight(group.getId())%>"><%=$SH("_rightnone", locale)%>
                        </form:radio><br/>
                        <form:radio name="<%=name%>" value="<%=Right.READ.name()%>" checked="<%=contentData.isGroupRight(group.getId(), Right.READ)%>"><%=$SH("_rightread", locale)%>
                        </form:radio><br/>
                        <form:radio name="<%=name%>" value="<%=Right.EDIT.name()%>" checked="<%=contentData.isGroupRight(group.getId(), Right.EDIT)%>"><%=$SH("_rightedit", locale)%>
                        </form:radio><br/>
                        <form:radio name="<%=name%>" value="<%=Right.APPROVE.name()%>" checked="<%=contentData.isGroupRight(group.getId(), Right.APPROVE)%>"><%=$SH("_rightapprove", locale)%>
                        </form:radio><br/>
                    </form:line>
                    <%}
                }%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close", locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save", locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>


