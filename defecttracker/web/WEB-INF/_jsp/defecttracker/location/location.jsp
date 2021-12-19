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
<%@ page import="de.elbe5.defecttracker.location.LocationData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.defecttracker.defect.DefectData" %>
<%@ page import="de.elbe5.defecttracker.ViewFilter" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    LocationData location = rdata.getCurrentContent(LocationData.class);
    assert (location != null);
    int id=location.getId();
    ViewFilter filter = ViewFilter.getFilter(rdata);
    List<DefectData> defects = filter.getLocationDefects(id);
%>
<form:message/>
<section class="contentTop">
    <h1>
        <%=$SH("_location",locale)%>&nbsp;<%=$H(location.getDisplayName())%>
    </h1>
</section>
<section class="contentSection tableContent" id="content">
    <% if (location.hasUserAnyEditRight(rdata)){%>
    <div class = contentTop>
        <a class="btn btn-outline-primary" href="/ctrl/defect/openCreateContentFrontend?parentId=<%=location.getId()%>"><%=$SH("_createDefect",locale)%>
        </a>
    </div>
    <%}%>
    <div id="defectTable" class="flexTable defect-table">
        <div class="tableHead">
            <div class="tableRow">
                <div style="flex:1"><%=$SH("_id",locale)%>
                </div>
                <div style="flex:6"><%=$SH("_description",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DESCRIPTION%>");>&nbsp;</a>
                </div>
                <div style="flex:2"><%=$SH("_creation",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CREATION%>");>&nbsp;</a>
                </div>
                <div style="flex:2"><%=$SH("_due",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_DUE_DATE%>");>&nbsp;</a>
                </div>
                <div style="flex:2"><%=$SH("_closed",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_CLOSE_DATE%>");>&nbsp;</a>
                </div>
                <div style="flex:2"><%=$SH("_state",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_STATE%>");>&nbsp;</a>
                </div>
                <div style="flex:2"><%=$SH("_assigned",locale)%>
                    <a class="fa fa-sort" onclick=linkTo("/ctrl/location/sort/<%=id%>?sortType=<%=ViewFilter.TYPE_ASSIGNED%>");>&nbsp;</a>
                </div>
                <div style="flex:1"></div>
            </div>
        </div>
        <div class="tableBody">
        <% for (DefectData defect : defects){%>
        <div class="tableRow">
            <div><%=defect.getDisplayId()%></div>
            <div><%=StringUtil.toHtml(defect.getDescription())%></div>
            <div><%=StringUtil.toHtmlDate(defect.getCreationDate(),locale)%></div>
            <div><%=StringUtil.toHtmlDate(defect.getDueDate(),locale)%></div>
            <div><%=StringUtil.toHtmlDate(defect.getCloseDate(),locale)%></div>
            <div><%=$SH(defect.getState(),locale)%></div>
            <div><%=$H(defect.getAssignedName())%></div>
            <div>
                <a href="" class="fa fa-eye" title="<%=$SH("_show",locale)%>" onclick="return linkTo('/ctrl/content/show/<%=defect.getId()%>',null);"></a>
            </div>
        </div>
        <%
            }%>
        </div>
    </div>
    <% if (location.getPlan()!=null){%>
    <div class="imageBox">
        <img src="/ctrl/location/showDefectPlan/<%=location.getId()%>?planId=<%=location.getPlan().getId()%>" alt="" />
    </div>
    <%}%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/location/getReport/<%=location.getId()%>');"><%=$SH("_downloadPdf",locale)%>
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
