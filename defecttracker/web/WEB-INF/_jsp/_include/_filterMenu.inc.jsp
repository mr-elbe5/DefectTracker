<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%
  SessionRequestData rdata = SessionRequestData.getRequestData(request);
  ViewFilter filter= ViewFilter.getFilter(rdata);
%>

<ul class="nav filter justify-content-end">
  <% if (filter.isEditor()){%>
  <li>
    <a class="fa fa-users" onclick="return openModalDialog('/ctrl/project/openWatchFilter/<%=rdata.getId()%>');">&nbsp;<%=$SH("_showUserDefects")%>&nbsp;(<%=filter.getWatchedIds().size()%>)</a>
  </li>
  <%}%>
  <li>
    <a class="fa fa-filter" onclick="return openModalDialog('/ctrl/project/openStateFilter/<%=rdata.getId()%>');"><%=$SH("_filter")%></a>
  </li>
</ul>
