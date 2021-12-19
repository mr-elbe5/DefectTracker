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
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.file.ImageData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    int fileId=rdata.getInt("fileId");
%>
        <li class="images">
            <span>[<%=$SH("_images", locale)%>]</span>
            <ul>
                <%
                    List<ImageData> images = contentData.getFiles(ImageData.class);
                    for (ImageData image : images) {%>
                <li class="<%=fileId==image.getId() ? "current" : ""%>">
                    <div class="treeline">
                        <span class="treeImage" id="<%=image.getId()%>">
                            <%=image.getDisplayName()%>
                            <span class="hoverImage">
                                <img src="/ctrl/image/showPreview/<%=image.getId()%>" alt="<%=$H(image.getFileName())%>"/>
                            </span>
                        </span>
                        <div class="icons">
                            <a class="icon fa fa-eye" href="/ctrl/image/show/<%=image.getId()%>" target="_blank" title="<%=$SH("_view",locale)%>"> </a>
                            <a class="icon fa fa-download" href="/ctrl/image/download/<%=image.getId()%>" title="<%=$SH("_download",locale)%>"> </a>
                        </div>
                    </div>
                </li>
                <%
                    }%>
            </ul>
        </li>

