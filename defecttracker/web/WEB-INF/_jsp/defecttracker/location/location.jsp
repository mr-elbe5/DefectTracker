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
<%@ page import="de.elbe5.defecttracker.location.LocationData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.defecttracker.defect.DefectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);

    LocationData location = rdata.getCurrentContent(LocationData.class);
    assert (location != null);
    int id=location.getId();
    ViewFilter filter = ViewFilter.getFilter(rdata);
    List<DefectData> defects = filter.getLocationDefects(id);
%>
<form:message/>
<section class="contentTop">
    <h1>
        <%=$SH("_location")%>&nbsp;<%=$H(location.getDisplayName())%>
    </h1>
</section>
<section class="contentSection tableContent" id="content">
    <% if (location.hasUserAnyEditRight(rdata)){%>
    <div class = contentTop>
        <a class="btn btn-outline-primary" href="/ctrl/defect/openCreateContentFrontend?parentId=<%=location.getId()%>"><%=$SH("_createDefect")%>
        </a>
    </div>
    <%}%>
    <table id="defectTable" class="defect-table">
        <thead class="tableHead">
            <tr>
                <th style="width:5%"><%=$SH("_id")%>
                </th>
                <th style="width:18%"><%=$SH("_description")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DESCRIPTION%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_creationDate")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CREATION%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_editedBy")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/project/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CHANGER%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_changeDate")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/project/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CHANGE%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_due")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DUE_DATE%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_closed")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CLOSE_DATE%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_state")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_STATE%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_assigned")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_ASSIGNED%>");>&nbsp;</a>
                </th>
                <th style="width:9%"><%=$SH("_notified")%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_NOTIFIED%>");>&nbsp;</a>
                </th>
                <th style="width:5%"></th>
            </tr>
        </thead>
        <tbody class="tableBody">
        <% for (DefectData defect : defects){%>
            <tr class="tableRow">
                <td><%=defect.getDisplayId()%></td>
                <td><%=StringUtil.toHtml(defect.getDescription())%></td>
                <td><%=StringUtil.toHtmlDate(defect.getCreationDate())%></td>
                <td><%=StringUtil.toHtml(defect.getChangerName())%></td>
                <td><%=StringUtil.toHtmlDate(defect.getChangeDate())%></td>
                <td><%=StringUtil.toHtmlDate(defect.getDueDate())%></td>
                <td><%=StringUtil.toHtmlDate(defect.getCloseDate())%></td>
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
    <% if (location.getPlan()!=null){%>
    <div class="imageBox">
        <img src="/ctrl/location/showDefectPlan/<%=location.getId()%>?planId=<%=location.getPlan().getId()%>" alt="" />
    </div>
    <%}%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/location/getReport/<%=location.getId()%>');"><%=$SH("_downloadPdf")%>
        </button>
    </div>
</section>
<script type="text/javascript">
    let defectTable=new FlexTable($('#defectTable'),{
        tableHeight: '20rem'
    });
    defectTable.init();
    $(window).resize(function(){
        defectTable.resize();
    });
</script>
