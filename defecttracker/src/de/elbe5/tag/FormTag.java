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
import de.elbe5.view.IView;

import javax.servlet.jsp.JspException;
import java.io.Writer;

public class FormTag extends BaseTag {

    protected String url = "";
    protected String name = "";
    protected boolean multi = false;
    protected boolean ajax = false;
    protected String target = IView.MODAL_DIALOG_JQID;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    String preHtml = "<form action=\"{1}\" method=\"post\" id=\"{2}\" name=\"{3}\" accept-charset=\"UTF-8\"{4}>\n";
    String postHtml = "</form>\n";
    String ajaxHtml = "<script type=\"text/javascript\">\n" +
            "$('#{1}').submit(function (event) {\n" +
            "var $this = $(this);\n    " +
            "event.preventDefault();\n    " +
            "var params = $this.{2}();\n    " +
            "{3}('{4}', params,'{5}');\n  " +
            "});\n" +
            "</script>\n";

    @Override
    public int doStartTag() throws JspException {
        try {
            Writer writer = getWriter();
            writer.write(StringUtil.format(preHtml, url, name, name, multi ? " enctype=\"multipart/form-data\"" : ""));
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
            writer.write(postHtml);
            if (ajax) {
                writer.write(StringUtil.format(ajaxHtml,
                        name,
                        multi ? "serializeFiles" : "serialize",
                        multi ? "postMultiByAjax" : "postByAjax",
                        url,
                        target));
            }
        } catch (Exception e) {
            Log.error("error writing form tag", e);
        }
        return EVAL_PAGE;
    }

}
