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
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="de.elbe5.user.UserCache" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.defecttracker.defect.*" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    DefectData defect = rdata.getCurrentContent(DefectData.class);
    assert(defect !=null);
    UserData assignedUser = UserCache.getUser(defect.getAssignedId());
    String assignedName = assignedUser == null ? "" : assignedUser.getName();
    List<FileData> files = new ArrayList<>();
    files.addAll(defect.getFiles(DefectDocumentData.class));
    files.addAll(defect.getFiles(DefectImageData.class));
    List<DefectCommentDocumentData> defectCommentDocuments=defect.getFiles(DefectCommentDocumentData.class);
    List<DefectCommentImageData> defectCommentImages=defect.getFiles(DefectCommentImageData.class);
    boolean isEditor=defect.hasUserEditRight(rdata);
    boolean isAssigned=rdata.getUserId()==defect.getAssignedId();
    if (isEditor || isAssigned){
%>
<form:message/>
<section class="contentSection" id="content">
    <div class="paragraph">
        <h3><%=$H(defect.getDescription())%></h3>
        <div class="d-flex flex-wrap align-items-stretch boxContainer">
            <div class="box">
                <div class="boxTitle"><%=$SH("_id",locale)%></div>
                <div class="boxText"><%=$I(defect.getDisplayId())%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_creator",locale)%></div>
                <div class="boxText"><%=$H(UserCache.getUser(defect.getCreatorId()).getName())%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_creationDate",locale)%></div>
                <div class="boxText"><%=StringUtil.toHtmlDateTime(defect.getCreationDate(),locale)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_phase",locale)%></div>
                <div class="boxText"><%=$SH(defect.getPhase(),locale)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_assigned",locale)%></div>
                <div class="boxText"><%=$H(assignedName)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_lot",locale)%></div>
                <div class="boxText"><%=$H(defect.getLot())%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_state",locale)%></div>
                <div class="boxText"><%=$SH(defect.getState(),locale)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_dueDate1",locale)%></div>
                <div class="boxText"><%=StringUtil.toHtmlDate(defect.getDueDate1(),locale)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_dueDate2",locale)%></div>
                <div class="boxText"><%=StringUtil.toHtmlDate(defect.getDueDate2(),locale)%></div>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_closeDate",locale)%></div>
                <div class="boxText"><%=StringUtil.toHtmlDate(defect.getCloseDate(),locale)%></div>
            </div>
        </div>
        <div class="d-flex flex-wrap align-items-stretch boxContainer">
            <div class="box">
                <div class="boxTitle"><%=$SH("_position",locale)%></div>
                <% if (defect.getPlanId()!=0){%>
                <div class="boxImage"><a href="#" onclick="return openModalDialog('/ctrl/defect/openFullDefectPlan/<%=defect.getId()%>');"><img src="/ctrl/defect/showCroppedDefectPlan/<%=defect.getId()%>" alt="" /></a></div>
                <%}%>
            </div>
            <div class="box">
                <div class="boxTitle"><%=$SH("_positionComment",locale)%></div>
                <div class="boxText"><%=StringUtil.toHtmlMultiline(defect.getPositionComment())%></div>
            </div>
        </div>

        <% if (!files.isEmpty()){%>
        <div class="d-flex flex-wrap align-items-stretch boxContainer">
            <% for (FileData file : files){%>
            <div class="box">
                <div class="boxTitle"><%=StringUtil.toHtml(file.getDisplayName())%></div>
                <div class="boxImage">
                    <% if (file.isImage()){%>
                    <a href="/ctrl/image/show/<%=file.getId()%>" target="_blank" title="<%=$SH("_view",locale)%>"><img src="/ctrl/image/showPreview/<%=file.getId()%>" alt="" /></a>
                    <%} else{%>
                    <a href="/ctrl/document/show/<%=file.getId()%>" target="_blank" title="<%=$SH("_view",locale)%>"><img src="/static-content/img/document.png" alt="" /></a>
                    <%}%></div>
                <div class="boxSubtitle"><%=StringUtil.toHtmlMultiline(file.getDescription())%></div>
            </div>
            <%}%>
        </div>
        <%}%>
    </div>
    <% for (DefectCommentData comment : defect.getComments()){%>
    <div class="paragraph">
        <div class="boxContainer">
            <div class="box">
                <div class="boxTitle"><%=$SH("_comment",locale)%>&nbsp;<%=$SH("_by",locale)%>&nbsp;<%=$H(UserCache.getUser(comment.getCreatorId()).getName())%>&nbsp;
                    <%=$SH("_ofDate",locale)%>&nbsp;<%=StringUtil.toHtmlDateTime(comment.getCreationDate(),locale)%> - <%=$SH("_state",locale)%>:<%=$SH(comment.getState(),locale)%>
                </div>
                <div class="boxText"><%=StringUtil.toHtmlMultiline(comment.getComment())%></div>
            </div>
        </div>
        <%
            files.clear();
            for (DefectCommentDocumentData file : defectCommentDocuments){
                if (file.getCommentId()==comment.getId())
                    files.add(file);
            }
            for (DefectCommentImageData file : defectCommentImages){
                if (file.getCommentId()==comment.getId())
                    files.add(file);
            }
        if (!files.isEmpty()){%>
        <div class="d-flex flex-wrap align-items-stretch boxContainer">
            <% for (FileData file : files){
            %>
            <div class="box">
                <div class="boxTitle"><%=StringUtil.toHtml(file.getDisplayName())%></div>
                <div class="boxImage">
                    <% if (file.isImage()){%>
                    <a href="/ctrl/image/show/<%=file.getId()%>" target="_blank" title="<%=$SH("_view",locale)%>"><img src="/ctrl/image/showPreview/<%=file.getId()%>" alt="" /></a>
                    <%} else{%>
                    <a href="/ctrl/document/show/<%=file.getId()%>" target="_blank" title="<%=$SH("_view",locale)%>"><img src="/static-content/img/document.png" alt="" /></a>
                    <%}%></div>
                <div class="boxSubtitle"><%=StringUtil.toHtmlMultiline(file.getDescription())%></div>
            </div>
            <%}%>
        </div>
        <%}%>
    </div>
    <%
    }
    if (!defect.isClosed()){
        if (rdata.hasSystemRight(SystemZone.CONTENTEDIT)) {%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return openModalDialog('/ctrl/defect/openEditContentFrontend/<%=defect.getId()%>',null);"><%=$SH("_edit",locale)%>
        </button>
        <button type="button" class="btn btn-outline-secondary" onclick="return openModalDialog('/ctrl/defect/closeDefect/<%=defect.getId()%>',null);"><%=$SH("_closeDefect",locale)%>
        </button>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/defect/getDocxFile/<%=defect.getId()%>');"><%=$SH("_downloadWord",locale)%>
        </button>
    </div>
        <%
        }%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return openModalDialog('/ctrl/defect/openCreateDefectComment/<%=defect.getId()%>',null);"><%=$SH("_comment",locale)%>
        </button>
    </div>
    <%}%>
    <div class=buttonLine>
        <button type="button" class="btn btn-outline-secondary" onclick="return linkTo('/ctrl/defect/getPdfFile/<%=defect.getId()%>');"><%=$SH("_downloadPdf",locale)%>
        </button>
    </div>
</section>
<%}%>
