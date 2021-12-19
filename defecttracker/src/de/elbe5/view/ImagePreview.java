package de.elbe5.view;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.file.ImageData;
import de.elbe5.file.ImageBean;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.request.ResponseCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ImagePreview implements IView{

    private ImageData data;
    private boolean noCache=true;

    public ImagePreview(ImageData data) {
        this.data = data;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response) {
        process(context,rdata,response);
    }

    protected void process(ServletContext context, RequestData rdata, HttpServletResponse response) {
        BinaryFile file= ImageBean.getInstance().getBinaryPreviewFile(data.getId());
        if (file==null){
            response.setStatus(ResponseCode.NO_CONTENT);
            return;
        }
        response.setContentType("image/jpeg");
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
                String sb = "filename=\"" + file.getFileName() + '"';
                response.setHeader("Content-Disposition", sb);
                response.setHeader("Content-Length", Integer.toString(file.getBytes().length));
                out.write(file.getBytes());
            }
            out.flush();
        } catch (IOException e) {
            response.setStatus(ResponseCode.NO_CONTENT);
        }
    }
}
