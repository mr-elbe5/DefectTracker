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
<%@ page import="de.elbe5.user.UserCache" %>
<%@ page import="de.elbe5.file.DocumentData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    DocumentData documentData = rdata.getSessionObject(RequestData.KEY_DOCUMENT,DocumentData.class);
    assert (documentData != null);
    String url = "/ctrl/document/saveDocument/" + documentData.getId();
    boolean fileRequired= documentData.isNew();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editDocumentSettings",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="documentform" ajax="true" multi="true">
            <div class="modal-body">
                <form:formerror/>
                <form:line label="_idAndFileName"><%=$I(documentData.getId())%> - <%=$H(documentData.getFileName())%>
                </form:line>
                <form:line label="_creation"><%=$DT(documentData.getCreationDate(), locale)%> - <%=$H(UserCache.getUser(documentData.getCreatorId()).getName())%>
                </form:line>
                <form:line label="_lastChange"><%=$DT(documentData.getChangeDate(), locale)%> - <%=$H(UserCache.getUser(documentData.getChangerId()).getName())%>
                </form:line>

                <form:file name="file" label="_file" required="<%=fileRequired%>"/>
                <form:text name="displayName" label="_name" value="<%=$H(documentData.getDisplayName())%>"/>
                <form:textarea name="description" label="_description" height="3em"><%=$H(documentData.getDescription())%></form:textarea>
                <form:line label="_author"><%=$H(UserCache.getUser(documentData.getChangerId()).getName())%>
                </form:line>
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


