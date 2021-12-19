<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ViewFilter filter= ViewFilter.getFilter(rdata);
    if (filter.getProjectId()!=0){
        ProjectData project = ContentCache.getContent(filter.getProjectId(),ProjectData.class);
        if (project!=null && project.hasUserReadRight(rdata)){%>
<li class="nav-item">
    <a class="nav-link" href="<%=project.getUrl()%>"><%=$H(project.getDisplayName())%>
    </a>
</li>
<li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="" role="button" aria-haspopup="true" aria-expanded="false"><%=$SH("_locations",locale)%>
    </a>
    <div class="dropdown-menu">
        <% for (ContentData child : project.getChildren()){
            if (child.isActive()){
        %>
        <a class="dropdown-item" href="<%=child.getUrl()%>"><%=$H(child.getDisplayName())%></a>
        <%}
        }%>
    </div>
</li>
<%
        }
    }%>
