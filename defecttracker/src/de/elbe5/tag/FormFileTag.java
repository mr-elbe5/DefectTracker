/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class FormFileTag extends FormLineTag {

    private boolean multiple=false;

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    String controlPreHtml = "<input type=\"file\" class=\"form-control-file\" id=\"{1}\" name=\"{2}\" {3}>";

    protected String getPreControlHtml(HttpServletRequest request, Locale locale) {
        return StringUtil.format(controlPreHtml, name, name, multiple ? "multiple" : "");
    }

}
