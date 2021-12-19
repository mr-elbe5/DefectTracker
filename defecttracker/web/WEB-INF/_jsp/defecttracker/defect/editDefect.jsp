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
<%@ page import="de.elbe5.defecttracker.defect.DefectData" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="de.elbe5.defecttracker.location.LocationData" %>
<%@ page import="de.elbe5.group.GroupData" %>
<%@ page import="de.elbe5.group.GroupBean" %>
<%@ page import="de.elbe5.user.UserCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    DefectData defect = rdata.getCurrentContent(DefectData.class);
    assert(defect !=null);
    LocationData location= ContentCache.getContent(defect.getLocationId(),LocationData.class);
    assert(location!=null);
    ProjectData project= ContentCache.getContent(defect.getProjectId(),ProjectData.class);
    assert(project!=null);
    GroupData group= GroupBean.getInstance().getGroup(project.getGroupId());
    String url = "/ctrl/defect/saveContentFrontend/" + defect.getId();
    if (defect.hasUserAnyEditRight(rdata)){
%>
<form:message/>
<section class="contentTop">
    <h1>
        <%=$H(defect.getDisplayName())%>
    </h1>
</section>
<section class="contentSection" id="content">
    <form:form url="<%=url%>" name="pageform" multi="true">
        <form:formerror/>
        <form:line label="_id" padded="true"><%=Integer.toString(defect.getDisplayId())%></form:line>
        <form:line label="_description" padded="true"><%=$HML(defect.getDescription())%></form:line>
        <form:select name="assigned" label="_assigned" required="true">
            <option value="0" <%=defect.getAssignedId()==0 ? "selected" : ""%>><%=$SH("_pleaseSelect", locale)%></option>
            <% for (int userId : group.getUserIds()){
                UserData user= UserCache.getUser(userId);
            %>
            <option value="<%=userId%>" <%=defect.getAssignedId()==user.getId() ? "selected" : ""%>><%=$H(user.getName())%></option>
            <%}%>
        </form:select>
        <form:text name="lot" label="_lot" value="<%=$H(defect.getLot())%>" />
        <form:text name="costs" label="_costs" value="<%=defect.getCostsString()%>" />
        <form:date name="dueDate2" label="_dueDate2" value="<%=$D(defect.getDueDate2(),locale)%>" required="true"/>
        <% if (defect.getPlanId()!=0){%>
        <form:line label="_position"><img src="/ctrl/defect/showCroppedDefectPlan/<%=defect.getId()%>" alt="" /></form:line>
        <%}%>
        <form:line label="_positionComment" padded="true"><%=$HML(defect.getPositionComment())%></form:line>
        <form:file name="files" label="_addDocumentsAndImages" required="false" multiple="true"/>
        <div>
            <button type="button" class="btn btn-outline-secondary" onclick="linkTo('/ctrl/defect/show/<%=defect.getId()%>');"><%=$SH("_cancel",locale)%>
            </button>
            <button type="submit" class="btn btn-primary"><%=$SH("_save",locale)%>
            </button>
        </div>
    </form:form>
</section>
<%}%>





