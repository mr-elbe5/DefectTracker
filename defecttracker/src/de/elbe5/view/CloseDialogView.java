package de.elbe5.view;

import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CloseDialogView extends UrlView {

    private String targetId = "";

    public CloseDialogView(String url) {
        super(url);
    }

    public CloseDialogView(String url, String targetId) {
        super(url);
        this.targetId = targetId;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        rdata.put(SessionRequestData.KEY_URL, url);
        if (!targetId.isEmpty())
            rdata.put(RequestData.KEY_TARGETID, targetId);
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/closeDialog.ajax.jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(ResponseCode.NOT_FOUND);
        }
    }
}
