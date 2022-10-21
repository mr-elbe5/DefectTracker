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
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.file.DocumentData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);

    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    int fileId=rdata.getInt("fileId");
%>
        <li class="documents">
            <span>[<%=$SH("_documents")%>]</span>
            <ul>
                <%
                    List<DocumentData> documents = contentData.getFiles(DocumentData.class);
                    for (DocumentData document : documents) {%>
                <li class="<%=fileId==document.getId() ? "current" : ""%>">
                    <div class="treeline">
                        <span id="<%=document.getId()%>">
                            <%=document.getDisplayName()%>
                        </span>
                        <div class="icons">
                            <a class="icon fa fa-eye" href="/ctrl/document/show/<%=document.getId()%>" target="_blank" title="<%=$SH("_view")%>"> </a>
                            <a class="icon fa fa-download" href="/ctrl/document/download/<%=document.getId()%>" title="<%=$SH("_download")%>"> </a>
                        </div>
                    </div>
                </li>
                <%}%>
            </ul>
        </li>


