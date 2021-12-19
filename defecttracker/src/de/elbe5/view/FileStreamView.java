package de.elbe5.view;

import de.elbe5.base.data.BinaryStreamFile;
import de.elbe5.file.FileData;
import de.elbe5.file.FileBean;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class FileStreamView  implements IView {

    private FileData data;
    private boolean forceDownload;
    private boolean noCache=true;

    public FileStreamView(FileData data, boolean forceDownload) {
        this.data = data;
        this.forceDownload = forceDownload;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response) {
        process(context,rdata,response);
    }

    protected void process(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        BinaryStreamFile file= FileBean.getInstance().getBinaryStreamFile(data.getId());
        if (file==null){
            response.setStatus(ResponseCode.NO_CONTENT);
            return;
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            contentType = "*/*";
        }
        StringBuilder contentDisposition = new StringBuilder();
        if (forceDownload) {
            contentDisposition.append("attachment;");
        }
        contentDisposition.append("filename=\"");
        contentDisposition.append(file.getFileName());
        contentDisposition.append('"');
        if (noCache) {
            response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
        }
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", contentDisposition.toString());
        try {
            OutputStream out = response.getOutputStream();
            file.writeToStream(out);
            out.flush();
        } catch (IOException e) {
            response.setStatus(ResponseCode.NO_CONTENT);
        }
    }

}
