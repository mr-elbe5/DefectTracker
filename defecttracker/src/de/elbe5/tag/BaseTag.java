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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.Writer;
import java.util.Locale;

public class BaseTag implements Tag {
    protected Tag parent = null;
    protected PageContext context = null;

    @Override
    public void setPageContext(PageContext pageContext) {
        context = pageContext;
    }

    public PageContext getContext() {
        return context;
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) getContext().getRequest();
    }

    public Locale getLocale(SessionRequestData rdata) {
        return rdata.getLocale();
    }

    public Writer getWriter() {
        return context.getOut();
    }

    @Override
    public void setParent(Tag tag) {
        parent = tag;
    }

    @Override
    public Tag getParent() {
        return parent;
    }

    @Override
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() {
        return EVAL_PAGE;
    }

    @Override
    public void release() {
    }

    protected String toHtml(String s) {
        return StringUtil.toHtml(s);
    }

    protected SessionRequestData getRequestData() {
        return (SessionRequestData) getRequest().getAttribute(SessionRequestData.KEY_REQUESTDATA);
    }

}
