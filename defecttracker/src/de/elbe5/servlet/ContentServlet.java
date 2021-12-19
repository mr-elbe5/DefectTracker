/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Configuration;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.view.IView;
import de.elbe5.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentServlet extends WebServlet {

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Configuration.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request);
        request.setAttribute(SessionRequestData.KEY_REQUESTDATA, rdata);
        rdata.readRequestParams();
        rdata.initSession();
        String url=request.getRequestURI().toLowerCase();
        try {
            IView result;
            if (url.endsWith(".html")) {
                result = ContentController.getInstance().show(request.getRequestURI(), rdata);
            }
            else{
                result=new RedirectView(getDefaultRoute(rdata));
            }
            if (rdata.hasCookies())
                rdata.setCookies(response);
            result.processView(getServletContext(), rdata, response);
        }
        catch (CmsException ce){
            handleException(request,response,ce);
        }
        catch (Exception e){
            handleException(request,response, new CmsInternalException(e.getMessage()));
        }
    }

    protected String getDefaultRoute(SessionRequestData rdata){
        ContentData contentData = ContentCache.getContentRoot();
        String url;
        if (contentData!=null)
            url=contentData.getUrl();
        else if (rdata.isLoggedIn())
            url="/ctrl/admin/openSystemAdministration";
        else
            url="/ctrl/user/openLogin";
        return url;
    }


}
