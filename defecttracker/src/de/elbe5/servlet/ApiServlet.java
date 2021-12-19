/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Configuration;
import de.elbe5.base.util.StringUtil;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.view.IApiView;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 3 / 2, maxRequestSize = 1024 * 1024 * 3)
public class ApiServlet extends HttpServlet {

    //for testing only
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(WebServlet.GET, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(WebServlet.POST,request, response);
    }

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Configuration.ENCODING);
        ApiRequestData rdata = new ApiRequestData(method, request);
        request.setAttribute(SessionRequestData.KEY_REQUESTDATA, rdata);
        rdata.tryLogin();
        String uri = request.getRequestURI();
        // skip "/api/"
        StringTokenizer stk = new StringTokenizer(uri.substring(5), "/", false);
        String methodName = "";
        ApiController controller = null;
        if (stk.hasMoreTokens()) {
            String controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    rdata.setId(StringUtil.toInt(stk.nextToken()));
                }
            }
            controller = ApiControllerCache.getApiController(controllerName);
        }
        rdata.readRequestParams();
        try {
            IApiView result = getView(controller, methodName, rdata);
            result.processView(getServletContext(), rdata, response);
        } catch (Exception e) {
            response.setStatus(ResponseCode.BAD_REQUEST);
        }
    }

    public IApiView getView(ApiController controller, String methodName, ApiRequestData rdata) {
        if (controller==null)
            throw new CmsRequestException();
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, ApiRequestData.class);
            Object result = controllerMethod.invoke(controller, rdata);
            if (result instanceof IApiView)
                return (IApiView) result;
            throw new CmsRequestException();
        } catch (NoSuchMethodException | InvocationTargetException e){
            throw new CmsRequestException();
        }
        catch (IllegalAccessException e) {
            throw new CmsAuthorizationException();
        }
    }

}
