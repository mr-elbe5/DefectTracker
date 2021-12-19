package de.elbe5.view;

import de.elbe5.application.Configuration;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

public class XmlView extends TextView {

    public XmlView(String text) {
        super(text);
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response) {
        response.setContentType(MessageFormat.format("text/xml;charset={0}", Configuration.ENCODING));
        if (!sendTextResponse(response))
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
