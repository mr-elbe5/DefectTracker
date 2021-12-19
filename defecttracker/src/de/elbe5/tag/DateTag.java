/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.util.StringUtil;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class DateTag extends FormLineTag {

    private String value = "";

    public void setValue(String value) {
        this.value = value;
    }

    String controlPreHtml =
            "<div class=\"input-group date\">\n" +
            "  <input type=\"text\" id=\"{1}\" name=\"{2}\" class=\"form-control datepicker\" value=\"{3}\" />\n" +
            "</div>\n" +
            "<script type=\"text/javascript\">$('#{4}').datepicker({language: '{5}'});</script>\n";

    protected String getPreControlHtml(HttpServletRequest request, Locale locale) {
        SessionRequestData rdata = SessionRequestData.getRequestData(request);
        return StringUtil.format(controlPreHtml, name, name, StringUtil.toHtml(value),name,rdata.getLocale().getLanguage());
    }

}
