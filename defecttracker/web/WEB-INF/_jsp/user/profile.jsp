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
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.user.UserBean" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
%>
<form:message/>
<section class="contentTop">
    <h1>
        <%=$SH("_profile",locale)%>
    </h1>
</section>
<div class="row">
    <section class="col-md-8 contentSection">
        <div class="paragraph form">
            <form:line label="_id"><%=$I(user.getId())%>
            </form:line>
            <form:line label="_login"><%=$H(user.getLogin())%>
            </form:line>
            <form:line label="_title"><%=$H(user.getTitle())%>
            </form:line>
            <form:line label="_firstName"><%=$H(user.getFirstName())%>
            </form:line>
            <form:line label="_lastName"><%=$H(user.getLastName())%>
            </form:line>
            <form:line label="_notes"><%=$H(user.getNotes())%>
            </form:line>
            <form:line label="_portrait"><% if (user.hasPortrait()) {%><img src="/ctrl/user/showPortrait/<%=user.getId()%>" alt="<%=$H(user.getName())%>"/> <%}%>
            </form:line>
            <h3><%=$SH("_address",locale)%>
            </h3>
            <form:line label="_street"><%=$H(user.getStreet())%>
            </form:line>
            <form:line label="_zipCode"><%=$H(user.getZipCode())%>
            </form:line>
            <form:line label="_city"><%=$H(user.getCity())%>
            </form:line>
            <form:line label="_country"><%=$H(user.getCountry())%>
            </form:line>
            <h3><%=$SH("_contact",locale)%>
            </h3>
            <form:line label="_email"><%=$H(user.getEmail())%>
            </form:line>
            <form:line label="_phone"><%=$H(user.getPhone())%>
            </form:line>
            <form:line label="_fax"><%=$H(user.getFax())%>
            </form:line>
            <form:line label="_mobile"><%=$H(user.getMobile())%>
            </form:line>
        </div>
    </section>
    <aside class="col-md-4 asideSection">
        <div class="section">
            <div class="paragraph form">
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangePassword');"><%=$SH("_changePassword",locale)%>
                    </a>
                </div>
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangeProfile');"><%=$SH("_changeProfile",locale)%>
                    </a>
                </div>
            </div>
        </div>
    </aside>
</div>
