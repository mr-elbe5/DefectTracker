package de.elbe5.view;

import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UrlView implements IView {

    protected String url;

    public UrlView(String url) {
        this.url=url;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        RequestDispatcher rd = context.getRequestDispatcher(url);
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(ResponseCode.NOT_FOUND);
        }
    }
}
