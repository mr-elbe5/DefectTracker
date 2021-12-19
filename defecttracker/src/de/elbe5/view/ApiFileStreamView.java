package de.elbe5.view;

import de.elbe5.file.FileData;
import de.elbe5.request.ApiRequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ApiFileStreamView extends FileStreamView implements  IApiView {

    public ApiFileStreamView(FileData data, boolean forceDownload) {
        super(data, forceDownload);
    }

    @Override
    public void processView(ServletContext context, ApiRequestData rdata, HttpServletResponse response) {
        process(context,rdata,response);
    }
}
