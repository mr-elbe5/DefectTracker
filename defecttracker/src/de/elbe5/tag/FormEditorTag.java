/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.data.Strings;
import de.elbe5.base.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

public class FormEditorTag extends FormLineTag {

    protected String type = "text";
    //_key or encoded
    protected String hint = "";
    protected String height = "";

    public void setType(String type) {
        this.type = type;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    String controlPreHtml = "<textarea id=\"{1}\" name=\"{2}\" data-editor=\"{3}\" data-gutter=\"1\" {4}>";
    String controlPostHtml = "</textarea>\n<small id=\"{1}Hint\" class=\"form-text text-muted\">{2}</small>\n";

    @Override
    protected String getPreControlHtml(HttpServletRequest request) {
        return StringUtil.format(controlPreHtml, name, name, type, height.isEmpty() ? "" : "style=\"height:" + height + "\"");
    }

    @Override
    protected String getPostControlHtml(HttpServletRequest request) {
        return StringUtil.format(controlPostHtml, name, hint.startsWith("_") ? Strings.html(hint) : hint);
    }

}
