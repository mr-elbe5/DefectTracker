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
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.base.data.Pair" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentSessionContent();
    assert (contentData != null);
    //todo js
    String url = "/ctrl/content/saveChildPageRanking/" + contentData.getId();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_sortChildPages", locale)%>
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
                <form:line label="_name"><%=$H(contentData.getDisplayName()) + "&nbsp;(" + contentData.getId() + ')'%>
                </form:line>
                <%
                    List<Pair<Integer, String>> childSortList = new ArrayList<>();
                    for (ContentData subpage : ContentCache.getContent(contentData.getId()).getChildren()) {
                        childSortList.add(new Pair<>(subpage.getId(), subpage.getName()));
                    }
                    String name, onchange;%>
                <h3><%=$SH("_subcontents", locale)%>
                </h3>
                <form:line label="_name" padded="true"><%=$SH("_position", locale)%>
                </form:line>
                <%
                    int idx = 0;
                    for (Pair<Integer, String> child : childSortList) {
                        name = "select" + child.getKey();
                        onchange = "setRanking(" + child.getKey() + ");";
                %>
                <form:select name="<%=name%>" label="<%=$H(child.getValue())%>" onchange="<%=onchange%>">
                    <%
                        for (int i = 0; i < childSortList.size(); i++) {%>
                    <option value="<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
                    </option>
                    <%
                        }%>
                </form:select>
                <%
                        idx++;
                    }%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close", locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save", locale)%>
                </button>
            </div>
        </form:form>
    </div>
    <script type="text/javascript">

        function initRankingData() {
            $('select', '#pageform').each(function (i) {
                setRankVal($(this), i);
            });
        }

        function setRanking(childId) {
            let select = $('#select' + childId);
            let newRanking = parseInt(select.val());
            let oldRanking = parseInt(select.attr('data-ranking'));
            $('select', '#subpages').each(function (i) {
                let sel = $(this);
                if (sel.attr('id') === 'select' + childId) {
                    setRankVal(sel, newRanking);
                } else {
                    let val = parseInt(sel.val());
                    if (newRanking > oldRanking) {
                        if (val > oldRanking && val <= newRanking)
                            setRankVal(sel, val - 1);
                    } else {
                        if (val < oldRanking && val >= newRanking)
                            setRankVal(sel, val + 1);
                    }
                }
            });
        }

        function setRankVal(sel, val) {
            sel.val(val);
            sel.attr('data-ranking', val);
        }

        initRankingData();
    </script>
</div>


