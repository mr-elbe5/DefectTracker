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
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData rootContent = ContentCache.getContentRoot();
    assert(rootContent!=null);
%>
            <section class="treeSection">
                <% if (rdata.hasAnyContentRight()) { %>
                <div><input type="checkbox" <%=Configuration.isShowInactiveContent() ? "checked" : ""%> onchange="linkTo('/ctrl/admin/toggleInactiveContent');" />&nbsp;<%=$SH("_showInactiveContent", locale)%></div>
                <ul class="tree pagetree">
                    <% rootContent.displayTreeContent(pageContext,rdata);%>
                </ul>
                <%}%>
            </section>
            <script type="text/javascript">
                let $current = $('.current','.pagetree');
                if ($current){
                    let $parents=$current.parents('li');
                    $parents.addClass("open");
                }
            </script>


