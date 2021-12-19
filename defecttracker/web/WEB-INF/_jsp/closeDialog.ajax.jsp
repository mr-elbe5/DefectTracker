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
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    String url = rdata.getString(SessionRequestData.KEY_URL);
    String targetId = rdata.getString(RequestData.KEY_TARGETID);
    String msg = rdata.getString(RequestData.KEY_MESSAGE);
    String msgType = rdata.getString(RequestData.KEY_MESSAGETYPE);
    if (targetId.isEmpty()) {%>
<div id="pageContent">

    <form action="<%=url%>" method="POST" id="forwardform" accept-charset="UTF-8">
        <%if (!msg.isEmpty()) {%>
        <input type="hidden" name="<%=RequestData.KEY_MESSAGE%>" value="<%=$H(msg)%>"/>
        <input type="hidden" name="<%=RequestData.KEY_MESSAGETYPE%>" value="<%=$H(msgType)%>"/>
        <%}%>
    </form>

</div>
<script type="text/javascript">
    $('#forwardform').submit();
</script>
<%} else {
    StringBuilder sb = new StringBuilder("{");
    if (!msg.isEmpty()) {
        sb.append(RequestData.KEY_MESSAGE).append(" : '").append($JS(msg)).append("',");
        sb.append(RequestData.KEY_MESSAGETYPE).append(" : '").append($JS(msgType)).append("'");
    }
    sb.append("}");%>
<div id="pageContent"></div>
<script type="text/javascript">
    let $dlg = $(MODAL_DLG_JQID);
    $dlg.html('');
    $dlg.modal('hide');
    $('.modal-backdrop').remove();
    postByAjax('<%=url%>', <%=sb.toString()%>, '<%=StringUtil.toJs(targetId)%>');
</script>
<%}%>
