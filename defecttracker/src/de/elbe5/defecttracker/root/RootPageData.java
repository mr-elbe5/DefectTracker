/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.root;

import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;

public class RootPageData extends ContentData {

    public RootPageData() {
    }

    public boolean hasUserReadRight(SessionRequestData rdata) {
        return rdata.isLoggedIn();
    }

    // view

    @Override
    public String getContentDataJsp() {
        return "/WEB-INF/_jsp/defecttracker/root/editContentData.ajax.jsp";
    }

    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        Writer writer = context.getOut();
        writer.write("<div id=\"pageContent\" class=\"viewArea\">");
        context.include("/WEB-INF/_jsp/defecttracker/root/page.jsp");
        writer.write("</div>");
    }

    //used in jsp
    public void displayTreeContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        if (hasUserReadRight(rdata)) {
            //backup
            ContentData currentContent=rdata.getCurrentContent();
            rdata.setCurrentRequestContent(this);
            context.include("/WEB-INF/_jsp/defecttracker/root/treeContent.inc.jsp", true);
            //restore
            rdata.setCurrentRequestContent(currentContent);
        }
    }

}
