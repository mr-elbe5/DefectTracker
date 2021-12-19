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
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.Writer;

public class FormErrorTag extends BaseTag {

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            if (rdata.hasFormError()) {
                Writer writer = getWriter();
                writer.write("<div class=\"formError\">\n");
                writer.write(StringUtil.toHtmlMultiline(rdata.getFormError(false).getFormErrorString()));
                writer.write("</div>");
            }
        } catch (Exception e) {
            Log.error("error writing error tag", e);
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

}
