<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.defecttracker.location.LocationData" %>
<%
SessionRequestData rdata = SessionRequestData.getRequestData(request);
ViewFilter filter= ViewFilter.getFilter(rdata);
ProjectData currentProject =  ContentCache.getContent(filter.getProjectId(), ProjectData.class);
int currentLocationId = rdata.getId();
%>
  <select class="form-select"  id="projectId" style="width: auto;" onchange="projectChanged()">
    <% for (int projectId : filter.getOwnProjectIds()){
      ProjectData project = ContentCache.getContent(projectId, ProjectData.class);%>
    <option value="<%=projectId%>" <%=projectId==filter.getProjectId() ? "selected" : ""%>><%=$H(project.getDisplayName())%></option>
    <%}%>
  </select>
<% if (currentProject != null){%>
  <select class="form-select"  id="locationSelect" style="width: auto;" onchange="locationChanged()">
    <option value="0" <%=0==currentLocationId ? "selected" : ""%>><%=$SH("_all")%></option>
    <% for (ContentData child : currentProject.getChildren()){
      if (child instanceof LocationData){
        LocationData location = (LocationData) child;%>
      <option value="<%=location.getId()%>" <%=location.getId()==currentLocationId ? "selected" : ""%>><%=$H(location.getDisplayName())%></option>
    <%}
    }%>
  </select>
<%}%>
<% if (filter.isEditor()){%>
<button type="button" class="btn btn-white btn-outline-dark" onclick="return openModalDialog('/ctrl/project/openWatchFilter/<%=rdata.getId()%>');"><%=filter.getWatchedIds().size()%>&nbsp;watched Lots</button>
<%}%>
<select class="form-select"  id="includeClosed" style="width: auto;" onchange="closedChanged()">
  <option value="true" <%=filter.isShowClosed() ? "selected" : ""%>><%=$SH("_showClosedDefects")%></option>
  <option value="false" <%=!filter.isShowClosed() ? "selected" : ""%>><%=$SH("_showNoClosedDefects")%></option>
</select>

  <script type="text/javascript">
    function projectChanged(){
      let projectId = $('#projectId option:selected').val();
      linkTo('/ctrl/project/selectProject/' + projectId);
    }

    function locationChanged() {
      let locationId = $('#locationSelect option:selected').val();
      console.log(locationId);
      if (locationId === '0') {
        let projectId = $('#projectId option:selected').val();
        linkTo('/ctrl/project/selectProject/' + projectId);
      } else {
        linkTo('/ctrl/location/show/' + locationId);
      }
    }

    function closedChanged() {
      let value = $('#includeClosed option:selected').val();
      linkTo('/ctrl/project/changeShowClosed/' + <%=rdata.getId()%> + '?showClosed=' + value);
    }

  </script>