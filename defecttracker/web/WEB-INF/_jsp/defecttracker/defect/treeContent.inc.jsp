<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);

    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
%>
<li class="open">
    <span class="<%=contentData.isActive() ? "" : "inactive"%>">
        <%=$H(contentData.getDisplayName())%>
    </span>
    <%if (contentData.hasUserEditRight(rdata)) {%>
    <div class="icons">
        <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/content/show/<%=contentData.getId()%>');" title="<%=$SH("_view")%>"> </a>
        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/content/openEditContentData/<%=contentData.getId()%>');" title="<%=$SH("_edit")%>"> </a>
    </div>
    <%}%>
    <ul>
        <jsp:include page="/WEB-INF/_jsp/content/defect/treeContentDocuments.inc.jsp" flush="true" />
        <jsp:include page="/WEB-INF/_jsp/defecttracker/defect/treeContentImages.inc.jsp" flush="true" />
    </ul>
</li>

