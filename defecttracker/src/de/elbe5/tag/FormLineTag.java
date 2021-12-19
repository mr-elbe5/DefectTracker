/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.Writer;
import java.util.Locale;

public class FormLineTag extends BaseTag {

    protected String name = "";
    protected String label = "";
    protected boolean required = false;
    protected boolean padded = false;

    public void setName(String name) {
        this.name = name;
    }

    //_key or encoded
    public void setLabel(String label) {
        this.label = label;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setPadded(boolean padded) {
        this.padded = padded;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            Locale locale = getLocale(rdata);
            Writer writer = getWriter();
            writer.write("<div class=\"form-group row");
            if (rdata.hasFormErrorField(name))
                writer.write(" error");
            writer.write("\">\n");
            if (label.isEmpty()) {
                writer.write("<div class=\"col-md-3\"></div>");
            } else {
                writer.write("<label class=\"col-md-3 col-form-label\"");
                if (!name.isEmpty()) {
                    writer.write(" for=\"");
                    writer.write(toHtml(name));
                    writer.write("\"");
                }
                writer.write(">");
                writer.write(label.startsWith("_") ? Strings.html(label, locale) : label);
                if (required) {
                    writer.write(" <sup>*</sup>");
                }
                writer.write("</label>\n");
            }
            writer.write("<div class=\"col-md-9");
            if (padded)
                writer.write(" padded");
            writer.write("\">\n");
            writer.write(getPreControlHtml(request, locale));
        } catch (Exception e) {
            Log.error("error writing form line tag", e);
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {
        try {
            HttpServletRequest request = getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            Locale locale = getLocale(rdata);
            Writer writer = getWriter();
            writer.write(getPostControlHtml(request, locale));
            writer.write("</div></div>");
        } catch (Exception e) {
            Log.error("error writing form line tag", e);
        }
        return EVAL_PAGE;
    }

    protected String getPreControlHtml(HttpServletRequest request, Locale locale) {
        return "";
    }

    protected String getPostControlHtml(HttpServletRequest request, Locale locale) {
        return "";
    }

}
