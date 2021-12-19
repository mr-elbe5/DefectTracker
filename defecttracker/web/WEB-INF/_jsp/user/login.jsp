<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.defecttracker.project.ProjectData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>

<%
    String title = Configuration.getAppTitle();
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    List<ProjectData> projects = ContentCache.getContents(ProjectData.class);
    assert(projects!=null);
%>
<html lang="<%=locale.getLanguage()%>">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/static-content/css/bandika.css?v=200527"/>
    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>

</head>
<body class="login">
<main id="main" role="main">
    <div class="container">
        <form:message/>
        <section class="mainSection loginSection">
            <form class="form" action="/ctrl/user/login" method="post" name="loginForm" accept-charset="UTF-8">
                <img class="mb-4" src="/static-content/img/logo-dark.png" alt="<%=Configuration.getAppTitle()%>">
                <div class="form-group">
                    <label for="login" class="sr-only"><%=$SH("_loginName",locale)%>
                    </label>
                    <input type="text" id="login" name="login" class="form-control"
                           placeholder="<%=$SH("_loginName",locale)%>" required autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only"><%=$SH("_password",locale)%>
                    </label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="<%=$SH("_password",locale)%>" required>
                </div>
                <div class="form-group">
                    <label for="language" class="sr-only"><%=$SH("_language",locale)%></label>
                    <select id="language" name="language" class="form-control">
                        <option value="<%=Locale.GERMAN.getLanguage()%>" selected><%=$H(Locale.GERMAN.getDisplayName(Locale.GERMAN))%></option>
                        <option value="<%=Locale.ENGLISH.getLanguage()%>"><%=$H(Locale.ENGLISH.getDisplayName(Locale.ENGLISH))%></option>
                    </select>
                </div>
                <button class="btn btn-outline-primary" type="submit"><%=$SH("_login",locale)%>
                </button>
            </form>
        </section>
    </div>
</main>
</body>
</html>
