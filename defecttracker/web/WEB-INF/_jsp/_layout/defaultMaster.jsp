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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="java.util.List" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    List<Integer> parentIds = ContentCache.getParentContentIds(contentData);
    String title = Configuration.getAppTitle()+ (contentData!=null ? " | " + contentData.getDisplayName() : "");
%>
<!DOCTYPE html>
<html lang="<%=locale.getLanguage()%>">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <title><%=$H(title)%></title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/static-content/css/bandika.css?v=200527"/>
    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="/static-content/js/locales/bootstrap-datepicker.de.js"></script>
    <script type="text/javascript" src="/static-content/js/ace.js"></script>
    <script type="text/javascript" src="/static-content/js/bandika-webbase.js"></script>
</head>
<body>
<header>
    <div class="container">
        <div class="top row">
            <section class="col-12 sysnav">
                <jsp:include page="/WEB-INF/_jsp/_include/_sysnav.inc.jsp" flush="true"/>
            </section>
        </div>
        <div class="menu row">
            <section class="col-12 menu">
                <nav class="navbar navbar-expand-lg navbar-light">
                    <a class="navbar-brand" href="/"><img src="/static-content/img/logo-dark.png" alt="Home" /></a>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <jsp:include page="/WEB-INF/_jsp/_include/_defectnav.inc.jsp" flush="true"/>
                        </ul>
                    </div>
                </nav>
            </section>
        </div>
        <div class="breadcrumbheader row">
            <section class="col-6 bc">
                <ol class="breadcrumb">
                    <%for (int i = parentIds.size() - 1; i >= 0; i--) {
                        ContentData content = ContentCache.getContent(parentIds.get(i));
                        if (content != null) {%>
                    <li class="breadcrumb-item">
                        <a href="<%=content.getUrl()%>"><%=$H(content.getDisplayName())%>
                        </a>
                    </li>
                    <%}}%>
                </ol>
            </section>
            <section class="col-6 filter">
                <ul class="filter">
                    <%if (contentData != null) {%>
                    <li><a class="fa fa-filter" href="" onclick="return openModalDialog('/ctrl/project/openFilter/<%=contentData.getId()%>');">&nbsp;<%=$SH("_filter",locale)%></a></li>
                    <%}%>
                </ul>
            </section>
        </div>
    </div>
</header>
<main id="main" role="main">
    <div id="pageContainer" class="container">
        <% if (contentData!=null) {
            try {
                contentData.displayContent(pageContext, rdata);
            } catch (Exception ignore) {
            }
        }%>
    </div>
</main>
<footer>
    <div class="container">
        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link"><%=$SH("_copyright", locale)%>
                </a>
            </li>
            <% for (ContentData data : ContentCache.getFooterList()) {
                if (data.hasUserReadRight(rdata)) {%>
            <li class="nav-item">
                <a class="nav-link" href="<%=data.getUrl()%>"><%=$H(data.getDisplayName())%>
                </a>
            </li>
            <%}
            }%>
        </ul>
    </div>
</footer>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
</body>
</html>
