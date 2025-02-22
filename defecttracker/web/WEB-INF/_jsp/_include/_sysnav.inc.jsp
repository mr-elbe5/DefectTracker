<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);

    String userClass=rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
%>
<ul class="nav justify-content-end">
    <li class="nav-item"><a class="nav-link fa fa-home" href="/" title="<%=$SH("_home")%>"></a></li>
    <%if (rdata.hasAnyElevatedSystemRight()) {%>
    <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openAdministration" title="<%=$SH("_administration")%>"></a></li>
    <%}%>
    <li class="nav-item">
        <a class="nav-link fa <%=userClass%>" data-toggle="dropdown" title="<%=$SH("_user")%>"></a>
        <div class="dropdown-menu">
            <% if (rdata.isLoggedIn()) {%>
            <a class="dropdown-item" href="/ctrl/user/openProfile"><%=$SH("_profile")%>
            </a>
            <a class="dropdown-item" href="/ctrl/user/logout"><%=$SH("_logout")%>
            </a>
            <% } else {%>
            <a class="dropdown-item" href="/ctrl/user/openLogin"><%=$SH("_login")%>
            </a>
            <%}%>
        </div>
    </li>
</ul>

