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
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();%>
<div id="pageContent">
    <form:message/>
    <section class="treeSection">
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=$SH("_system",locale)%>
                </a>
                <ul>
                    <%if (rdata.hasSystemRight(SystemZone.APPLICATION)) {%>
                    <li>
                        <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');"><%=$SH("_restart",locale)%>
                        </a>
                    <li class="open">
                        <a><%=$SH("_caches",locale)%>
                        </a>
                        <ul>
                            <li>
                                <span><%=$SH("_userCache",locale)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="<%=$SH("_reload",locale)%>"></a>
                                </div>
                            </li>
                            <li>
                                <span><%=$SH("_contentCache",locale)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="<%=$SH("_reload",locale)%>"></a>
                                </div>
                            </li>
                            <li>
                                <span><%=$SH("_fileCache",locale)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-recycle" href="/ctrl/admin/clearFileCache" title="<%=$SH("_clear",locale)%>"></a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <jsp:include page="../timer/timerAdministration.inc.jsp" flush="true"/>
                    <%}%>
                </ul>
            </li>
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>
