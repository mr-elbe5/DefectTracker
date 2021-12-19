package de.elbe5.view;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.file.FileData;
import de.elbe5.file.FileBean;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class FileView  implements IView {

    private FileData data;
    private boolean forceDownload;
    private boolean noCache=true;

    public FileView(FileData data, boolean forceDownload) {
        this.data = data;
        this.forceDownload = forceDownload;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        BinaryFile file= FileBean.getInstance().getBinaryFile(data.getId());
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
            if (data.getBytes() == null) {
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
