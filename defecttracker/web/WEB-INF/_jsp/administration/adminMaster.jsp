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
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    String title = rdata.getString(RequestData.KEY_TITLE);
    String includeUrl = rdata.getString(RequestData.KEY_JSP);
%>
<!DOCTYPE html>
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
    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="/static-content/js/locales/bootstrap-datepicker.de.js"></script>
    <script type="text/javascript" src="/static-content/js/ace.js"></script>
    <script type="text/javascript" src="/static-content/js/bandika-webbase.js"></script>
</head>

<body class="admin">
<header>
    <div class="container">
        <div class="top row">
            <section class="col-12 sysnav">
                <ul class="nav justify-content-end">
                    <li class="nav-item"><a class="nav-link fa fa-home" href="/" title="<%=$SH("_home", locale)%>"></a></li>
                </ul>
            </section>
        </div>
        <div class="menu row">
            <section class="col-12 menu">
                <nav class="navbar navbar-expand-lg navbar-light">
                    <span class="navbar-brand" ><a><%=$SH("_administration",locale)%></a></span>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <% if (rdata.hasSystemRight(SystemZone.APPLICATION)){%>
                            <li class="nav-item">
                                <a class="nav-link"
                                        href="/ctrl/admin/openSystemAdministration"><%=$SH("_systemAdministration",locale)%>
                                </a>
                            </li>
                            <%}%>
                            <% if (rdata.hasSystemRight(SystemZone.USER)){%>
                            <li class="nav-item">
                                <a class="nav-link"
                                        href="/ctrl/admin/openPersonAdministration"><%=$SH("_personAdministration",locale)%>
                                </a>
                            </li>
                            <%}%>
                            <% if (rdata.hasSystemRight(SystemZone.CONTENTEDIT)){%>
                            <li class="nav-item">
                                <a class="nav-link" href="/ctrl/admin/openContentAdministration"><%=$SH("_contentAdministration",locale)%>
                                </a>
                            </li>
                            <%}%>
                        </ul>
                    </div>
                </nav>
            </section>
        </div>
        <div>
            <section class="bc">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/"><%=$SH("_home",locale)%>
                        </a></li>
                        <li class="breadcrumb-item"><a><%=$H(title)%>
                        </a></li>
                    </ol>
                </nav>
            </section>
        </div>
    </div>
</header>
<main id="main" role="main">
    <div id="pageContainer" class="container">
        <jsp:include page="<%=includeUrl%>" flush="true"/>
    </div>
</main>
<footer>
    <div class="container">
        <ul class="nav"><li class="nav-item"><span class="nav-link"><%=$SH("_copyright",locale)%></span></li></ul>
    </div>
</footer>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog">
</div>
<script type="text/javascript">
    function confirmDelete() {
        return confirm('<%=$SJ("_confirmDelete",locale)%>');
    }

    function confirmExecute() {
        return confirm('<%=$SJ("_confirmExecute",locale)%>');
    }
</script>

</body>
</html>

