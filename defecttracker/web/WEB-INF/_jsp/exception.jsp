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
<%@ page import="de.elbe5.servlet.CmsException" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>

<%
    Locale locale = request.getLocale();
    CmsException ex=(CmsException) request.getAttribute("exception");
    assert(ex!=null);
    String error=$SH("_errorHeader",locale);
    String errorText=$SH("_error"+ex.getResponseCode(),locale);
%>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <title>Exception</title>
</head>
<style>
    body {
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        background: #f8f9fa url("/static-content/img/bricksred_bg.png");
        color: #343a40;
        font-family: Arial, Helvetica, sans-serif;
        font-size: 1rem;
    }

    main{
        max-width:360px;
        margin: 20% auto;
        padding: 30px;
        background: #f8f9fa;
        border: 1px solid #343a40;
        border-radius: 5px;
    }
    h1{
        text-align:center;
        font-size: 1.5rem;
    }
    .errorText{
        text-align: center;
        font-size: 1.2rem;
    }
    .link{
        padding-top: 2rem;
        text-align: center;
    }

</style>
<body>
<main>
    <h1><%=error%></h1>
    <div class="errorText"><%=errorText%></div>
    <div class="link"><a href="/" title="Home"><%=$SH("_home",locale)%></a></div>
</main>
</body>
</html>
