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
<%@ page import="de.elbe5.defecttracker.defect.DefectCommentData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    DefectData defect = ContentCache.getContent(rdata.getId(),DefectData.class);
    assert (defect != null);
    DefectCommentData comment = rdata.getSessionObject(DefectCommentData.KEY_COMMENT,DefectCommentData.class);
    String url = "/ctrl/defect/saveDefectComment/" + defect.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editDefectComment", locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="pageform" ajax="true" multi="true">
            <div class="modal-body">
                <form:formerror/>
                <form:line label="_defect" padded="true"><%=$H(defect.getDescription())%></form:line>
                <form:textarea name="comment" label="_comment" height="8em" required="true"><%=$H(comment.getComment())%></form:textarea>
                <form:select name="state" label="_state">
                    <option value="<%=DefectData.STATE_OPEN%>" <%=DefectData.STATE_OPEN.equals(defect.getState()) ? "selected" : ""%>><%=$SH(DefectData.STATE_OPEN,locale)%></option>
                    <option value="<%=DefectData.STATE_DISPUTED%>" <%=DefectData.STATE_DISPUTED.equals(defect.getState()) ? "selected" : ""%>><%=$SH(DefectData.STATE_DISPUTED,locale)%></option>
                    <option value="<%=DefectData.STATE_REJECTED%>" <%=DefectData.STATE_REJECTED.equals(defect.getState()) ? "selected" : ""%>><%=$SH(DefectData.STATE_REJECTED,locale)%></option>
                    <option value="<%=DefectData.STATE_DONE%>" <%=DefectData.STATE_DONE.equals(defect.getState()) ? "selected" : ""%>><%=$SH(DefectData.STATE_DONE,locale)%></option>
                </form:select>
                <form:file name="files" label="_addDocumentsAndImages" required="false" multiple="true"/>
                <form:line><%=$SH("_uploadHint", locale)%></form:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save",locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>





