package de.elbe5.view;

import de.elbe5.application.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonView implements IApiView {

    private String json;

    public JsonView(String json) {
        this.json=json;
    }

    @Override
    public void processView(ServletContext context, ApiRequestData rdata, HttpServletResponse response) {
        response.setContentType("application/json");
        if (!sendJsonResponse(response))
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    protected boolean sendJsonResponse(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        //Log.log("sending json: " + json);
        try {
            ServletOutputStream out = response.getOutputStream();
            if (json == null || json.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = json.getBytes(Configuration.ENCODING);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
            Log.error("json has been sent");
        } catch (IOException ioe) {
            Log.error("response error", ioe);
            return false;
        }
        return true;
    }
}
