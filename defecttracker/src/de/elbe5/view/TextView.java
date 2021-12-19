package de.elbe5.view;

import de.elbe5.base.log.Log;
import de.elbe5.application.Configuration;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public class TextView implements IView {

    private String text;
    private boolean noCache=true;

    public TextView(String text) {
        this.text = text;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response) {
        response.setContentType(MessageFormat.format("text/plain;charset={0}", Configuration.ENCODING));
        if (!sendTextResponse(response))
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    protected boolean sendTextResponse(HttpServletResponse response) {
        if (noCache) {
            response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
        }
        try {
            OutputStream out = response.getOutputStream();
            if (text == null || text.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = text.getBytes(Configuration.ENCODING);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
        } catch (IOException ioe) {
            Log.error("response error", ioe);
            return false;
        }
        return true;
    }
}
