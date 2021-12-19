/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;

import javax.servlet.jsp.JspException;
import java.io.Writer;

public class FormCheckTag extends BaseTag {

    protected String name = "";
    protected String value = "";
    protected boolean checked = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    protected String getPreHtml() {
        return "<span>\n  <input type=\"checkbox\" name=\"{1}\" value=\"{2}\" {3}/>\n  <label class=\"form-check-label\">";
    }

    protected String getPostHtml() {
        return "</label>\n</span>";
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            Writer writer = getWriter();
            writer.write(StringUtil.format(getPreHtml(), StringUtil.toHtml(name), StringUtil.toHtml(value), checked ? "checked" : ""));
        } catch (Exception e) {
            Log.error("error writing form tag", e);
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {
        try {
            Writer writer = getWriter();
            writer.write(getPostHtml());
        } catch (Exception e) {
            Log.error("error writing form tag", e);
        }
        return EVAL_PAGE;
    }

}
