<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
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
    assert (defect != null);
    LocationData location = ContentCache.getContent(defect.getLocationId(), LocationData.class);
    assert (location != null);
    ProjectData project = ContentCache.getContent(defect.getProjectId(), ProjectData.class);
    assert (project != null);
    GroupData group = GroupBean.getInstance().getGroup(project.getGroupId());
    String url = "/ctrl/defect/saveContentFrontend/" + defect.getId();

    if (defect.hasUserAnyEditRight(rdata)) {%>
<form:message/>
<section class="contentTop">
    <h1>
        <%=$H(defect.getDescription())%>
    </h1>
</section>
<section class="contentSection" id="content">
    <form:form url="<%=url%>" name="pageform" multi="true">
        <form:formerror/>
        <form:line label="_id" padded="true"><%=Integer.toString(defect.getDisplayId())%>
        </form:line>
        <form:textarea name="description" label="_description" height="5em" required="true"><%=StringUtil.toHtmlMultiline(defect.getDescription())%>
        </form:textarea>
        <form:select name="assigned" label="_assignTo" required="true">
            <option value="0" <%=defect.getAssignedId() == 0 ? "selected" : ""%>><%=$SH("_pleaseSelect", locale)%>
            </option>
            <% for (int userId : group.getUserIds()) {
                UserData user = UserCache.getUser(userId);%>
            <option value="<%=userId%>" <%=defect.getAssignedId() == user.getId() ? "selected" : ""%>><%=$H(user.getName())%>
            </option>
            <%}%>
        </form:select>
        <form:text name="lot" label="_lot" value="<%=$H(defect.getLot())%>"/>
        <form:text name="costs" label="_costs" value="<%=defect.getCostsString()%>"/>
        <form:date name="dueDate1" label="_dueDate" value="<%=StringUtil.toHtmlDate(defect.getDueDate1(),locale)%>" required="true"/>
        <% if (location.getPlan() != null) {%>
        <form:line label="_position"> </form:line>
        <div id="planContainer">
            <img id="plan" src="/ctrl/image/show/<%=defect.getPlanId()%>" alt="" style="border:1px solid red"/>
            <div id="planPositioner">
                <img id="arrow" src="/static-content/img/redarrow.svg" alt=""/>
                <span><%=defect.getDisplayId()%></span>
            </div>
        </div>
        <input type="hidden" name="positionX" id="positionX" value="<%=defect.getPositionX()%>"/>
        <input type="hidden" name="positionY" id="positionY" value="<%=defect.getPositionY()%>"/>
        <%}%>
        <form:textarea name="positionComment" label="_positionComment" height="5em"><%=StringUtil.toHtmlMultiline(defect.getPositionComment())%>
        </form:textarea>
        <form:file name="files" label="_addDocumentsAndImages" required="false" multiple="true"/>
        <form:line><%=$SH("_uploadHint", locale)%></form:line>
        <div>
            <button type="button" class="btn btn-outline-secondary" onclick="linkTo('/ctrl/location/show/<%=location.getId()%>');"><%=$SH("_cancel", locale)%>
            </button>
            <button type="submit" class="btn btn-primary"><%=$SH("_save", locale)%>
            </button>
        </div>
    </form:form>
</section>
<% if (location.getPlan() != null) {%>
<script type="text/javascript">
    let posX = 0;
    let posY = 0;
    let $container = $('#planContainer');
    let $positioner = $('#planPositioner');
    let $plan = $('#plan');

    $plan.on('click', function (event) {
        let position=$container.position();
        //console.log('container position=' + position.left + ',' + position.top);
        let offset = $container.offset();
        //console.log('container offset=' + offset.left + ',' + offset.top);
        posX = Math.round(event.pageX - offset.left );
        posY = Math.round(event.pageY - offset.top);
        //console.log('posX,posY=' + posX + ',' + posY);
        setPositioner();
    });

    function setPositioner() {
        let planPosition=$plan.position();
        $positioner.css('left', posX - 11);
        //relative, so go top
        $positioner.css('top', posY - 5 - $plan.height());
        // both percent * 100
        let positionX=Math.round(posX*100*100/$plan.width());
        let positionY=Math.round(posY*100*100/$plan.height());
        //console.log('positionX,positionY=' + positionX + ',' + positionY);
        $('#positionX').val(positionX);
        $('#positionY').val(positionY);
    }

    $('#arrow').load(function () {
        setPositioner($container.position());
    });

    $plan.load(function () {
        posX = Math.floor((<%=defect.getPositionX()%>)*$plan.width()/100/100);
        posY = Math.floor((<%=defect.getPositionY()%>)*$plan.height()/100/100);
        //console.log('posX,posY=' + posX + ',' + posY);
        setPositioner();
    });

</script>
<%}
}%>





