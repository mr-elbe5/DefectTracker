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
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    List<String> imageTypes=contentData.getImageClasses();
    int fileId=rdata.getInt("fileId");
%>
        <li class="images">
            <span>[<%=$SH("_images", locale)%>]</span>
            <%if (contentData.hasUserEditRight(rdata)) {%>
            <div class="icons">
                <% if (rdata.hasClipboardData(RequestData.KEY_IMAGE)) {%>
                <a class="icon fa fa-paste" href="/ctrl/image/pasteImage?parentId=<%=contentData.getId()%>" title="<%=$SH("_pasteImage",locale)%>"> </a>
                <%}
                    if (!imageTypes.isEmpty()) {%>
                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newImage",locale)%>"></a>
                <div class="dropdown-menu">
                    <%for (String imageType : imageTypes) {
                        String name = $SH(imageType, locale);%>
                    <a class="dropdown-item" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId=<%=contentData.getId()%>&type=<%=imageType%>');"><%=name%>
                    </a>
                    <%
                        }%>
                </div>
                <%
                    }%>
            </div>
            <%}%>
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
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/image/openEditImage/<%=image.getId()%>');" title="<%=$SH("_edit",locale)%>"> </a>
                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/image/cutImage/<%=image.getId()%>');" title="<%=$SH("_cut",locale)%>"> </a>
                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/image/copyImage/<%=image.getId()%>');" title="<%=$SH("_copy",locale)%>"> </a>
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/image/deleteImage/<%=image.getId()%>');" title="<%=$SH("_delete",locale)%>"> </a>
                        </div>
                    </div>
                </li>
                <%
                    }%>
            </ul>
        </li>

