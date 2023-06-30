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
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ page import="de.elbe5.defecttracker.defect.DefectData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ProjectData project = rdata.getCurrentContent(ProjectData.class);
    assert (project != null);

    int id=project.getId();
    ViewFilter filter = ViewFilter.getSessionFilter(rdata);
    List<DefectData> defects = filter.getProjectDefects();
%>
<% if (project.hasUserReadRight(rdata)){%>
<form:message/>
<section class="contentSection tableContent" id="content">
    <h3><%=$SH("_defects")%></h3>
    <table id="defectTable" class="defect-table">
        <thead>
        <tr>
            <th style="width:5%"><%=$SH("_id")%>
            </th>
            <th style="width:18%"><%=$SH("_description")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DESCRIPTION%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_location")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/project/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_LOCATION%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_creationDate")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CREATION%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_editedBy")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/project/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CHANGER%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_changeDate")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/project/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CHANGE%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_due")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DUE_DATE%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_closed")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CLOSE_DATE%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_state")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_STATE%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_assigned")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_ASSIGNED%>");>&nbsp;</a>
            </th>
            <th style="width:8%"><%=$SH("_notified")%>
                <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_NOTIFIED%>");>&nbsp;</a>
            </th>
            <th style="width:5%"></th>
        </tr>
        </thead>
        <tbody>
        <% for (DefectData defect : defects){%>
        <tr>
            <td><%=defect.getDisplayId()%></td>
            <td><%=$H(defect.getDescription())%></td>
            <td><%=$H(defect.getLocationName())%></td>
            <td><%=$DT(defect.getCreationDate())%></td>
            <td><%=$H(defect.getChangerName())%></td>
            <td><%=$DT(defect.getChangeDate())%></td>
            <td><%=$D(defect.getDueDate())%></td>
            <td><%=$D(defect.getCloseDate())%></td>
            <td><%=$SH(defect.getState())%></td>
            <td><%=$H(defect.getAssignedName())%></td>
            <td><%=$SH(defect.isNotified() ? "_yes" : "_no")%></td>
            <td>
                <a href="" class="fa fa-eye" title="<%=$SH("_show")%>" onclick="return linkTo('/ctrl/content/show/<%=defect.getId()%>',null);"></a>
            </td>
        </tr>
        <%
            }%>
        </tbody>
    </table>
    <% if (project.hasUserEditRight(rdata)){%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/project/getExcel/<%=project.getId()%>');"><%=$SH("_downloadExcel")%>
        </button>
    </div>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/project/getReport/<%=project.getId()%>');"><%=$SH("_downloadPdf")%>
        </button>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/project/getReport/<%=project.getId()%>?includeComments=true');"><%=$SH("_downloadPdfWithComments")%>
        </button>
    </div>
    <%}%>
</section>
<script type="text/javascript">
    let defectTable=new FlexTable($('#defectTable'),{
        $container: $('main')
    });
    defectTable.init();
    $(window).resize(function(){
        defectTable.resize();
    });
</script>
<%}%>
