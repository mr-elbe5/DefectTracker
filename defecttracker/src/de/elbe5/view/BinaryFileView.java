package de.elbe5.view;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class BinaryFileView implements IView{

    private BinaryFile file;
    private boolean forceDownload=false;
    private boolean noCache=true;

    public BinaryFileView(BinaryFile file) {
        this.file = file;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public void setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        process(context, rdata,response);
    }

    public void process(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        if (file==null){
            response.setStatus(ResponseCode.NO_CONTENT);
            return;
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            contentType = "*/*";
        }
        response.setContentType(contentType);
        try {
            OutputStream out = response.getOutputStream();
            if (file.getBytes() == null) {
                response.setHeader("Content-Length", "0");
            } else {
                if (noCache) {
                    response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Pragma", "no-cache");
                }
                StringBuilder sb = new StringBuilder();
                if (forceDownload) {
                    sb.append("attachment;");
                }
                sb.append("filename=\"");
                sb.append(file.getFileName());
                sb.append('"');
                response.setHeader("Content-Disposition", sb.toString());
                response.setHeader("Content-Length", Integer.toString(file.getBytes().length));
                out.write(file.getBytes());
            }
            out.flush();
        } catch (IOException e) {
            response.setStatus(ResponseCode.NO_CONTENT);
        }
    }
}
