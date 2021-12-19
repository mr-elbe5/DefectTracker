<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    String redirectUrl = rdata.getString("redirectUrl");
    if (redirectUrl != null && redirectUrl.length() > 0) {%>
<html>
<head><title></title></head>
<body>
&nbsp;
</body>
</html>
<script type="text/javascript">
    try {
        window.location.href = '<%=redirectUrl%>';
    } catch (e) {
    }
</script>
<%}%>

