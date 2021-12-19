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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.user.UserCache" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentSessionContent();
    assert (contentData != null);
    String url = "/ctrl/content/saveContentData/" + contentData.getId();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editContentData", locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="pageform" ajax="true" multi="true">
            <div class="modal-body">
                <form:formerror/>
                <h3><%=$SH("_settings", locale)%>
                </h3>
                <form:line label="_idAndUrl"><%=$I(contentData.getId())%> - <%=$H(contentData.getUrl())%>
                </form:line>
                <form:line label="_creation"><%=$DT(contentData.getCreationDate(), locale)%> - <%=$H(UserCache.getUser(contentData.getCreatorId()).getName())%>
                </form:line>
                <form:line label="_lastChange"><%=$DT(contentData.getChangeDate(), locale)%> - <%=$H(UserCache.getUser(contentData.getChangerId()).getName())%>
                </form:line>

                <form:text name="displayName" label="_name" required="true" value="<%=$H(contentData.getDisplayName())%>"/>
                <form:textarea name="description" label="_description" height="5em"><%=$H(contentData.getDescription())%></form:textarea>
                <form:select name="language" label="_language">
                    <% for (Locale loc : Configuration.getLocales()){%>
                    <option value="<%=loc.getLanguage()%>" <%=contentData.getLocale().equals(loc) ? "selected" : ""%>><%=$H(loc.getDisplayLanguage(locale))%>
                    </option>
                    <%}%>
                </form:select>
                <form:select name="accessType" label="_accessType">
                    <option value="<%=ContentData.ACCESS_TYPE_OPEN%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : ""%>><%=$SH("_$accessTypeOpen", locale)%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INHERITS%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : ""%>><%=$SH("_$accessTypeInherits", locale)%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INDIVIDUAL%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : ""%>><%=$SH("_$accessTypeIndividual", locale)%>
                    </option>
                </form:select>
                <form:select name="navType" label="_navType">
                    <option value="<%=ContentData.NAV_TYPE_NONE%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : ""%>><%=$SH("_$navTypeNone", locale)%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_HEADER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : ""%>><%=$SH("_$navTypeHeader", locale)%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_FOOTER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : ""%>><%=$SH("_$navTypeFooter", locale)%>
                    </option>
                </form:select>
                <form:line label="_active" padded="true">
                    <form:check name="active" value="true" checked="<%=contentData.isActive()%>"/>
                </form:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close", locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save", locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>


