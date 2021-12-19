<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%!
    public String $D(LocalDate date, Locale locale){
        return StringUtil.toHtmlDate(date, locale);
    }

    public String $T(LocalTime time, Locale locale){
        return StringUtil.toHtmlTime(time, locale);
    }

    public String $DT(LocalDateTime dateTime, Locale locale){
        return StringUtil.toHtmlDateTime(dateTime, locale);
    }

    public String $H(String src){
        return StringUtil.toHtml(src);
    }

    public String $JS(String src){
        return StringUtil.toJs(src);
    }

    public String $HML(String src){
        return StringUtil.toHtmlMultiline(src);
    }

    public String $SH(String key, Locale locale){
        return Strings.html(key,locale);
    }

    public String $SHM(String key, Locale locale){
        return Strings.htmlMultiline(key,locale);
    }

    public String $SJ(String key, Locale locale){
        return Strings.js(key,locale);
    }

    public String $I(int i){
        return Integer.toString(i);
    }
%>
