<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.request.SessionRequestData" %><%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<div id="pageContent">
    <form:message/>
    <jsp:include page="/WEB-INF/_jsp/content/contentAdministration.inc.jsp" flush="true" />
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>

