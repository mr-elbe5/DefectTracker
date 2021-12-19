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
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    int contentId=rdata.getId();
    String url = "/ctrl/project/setFilter/"+contentId;
    ViewFilter filter= ViewFilter.getFilter(rdata);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_setFilter", locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="filterform" ajax="true">
            <div class="modal-body">
                <form:formerror/>
                <form:select name="projectId" label="_projects" onchange="updateUsers()">
                    <% for (int projectId : filter.getOwnProjectIds()){
                        ProjectData project= ContentCache.getContent(projectId,ProjectData.class);
                        if (project==null)
                            continue;%>
                    <option value="<%=project.getId()%>" <%=filter.getProjectId()==project.getId() ? "selected" : ""%>><%=$H(project.getName())%>
                    </option>
                    <%}%>
                </form:select>
                <% if (filter.isEditor()){%>
                <form:select name="assignedId" label="_showUserDefects" >
                    <!-- empty -->
                </form:select>
                <%} else {%>
                    <input type="hidden" name="assignedId" value="0" />
                <%}%>
                <form:line label="_showClosedDefects" padded="true">
                    <form:check name="showClosed" value="true" checked="<%=filter.isShowClosed()%>"></form:check>
                </form:line>
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
<script type="text/javascript">
    let $projectSelect = $('#projectId');
    let $userSelect = $('#assignedId');

    function updateUsers(){
        if (!$userSelect)
            return;
        let projectId=$projectSelect.val();
        $.ajax({
            url: "/ctrl/project/updateFilterUsers", type: 'POST', data: {projectId: projectId}, cache: false, dataType: 'html'
        }).success(function (html) {
            $userSelect.html(html);
        });
    }

    updateUsers();

</script>


