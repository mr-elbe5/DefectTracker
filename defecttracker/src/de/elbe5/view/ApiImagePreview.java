package de.elbe5.view;

import de.elbe5.file.ImageData;
import de.elbe5.request.ApiRequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ApiImagePreview extends ImagePreview implements IApiView{

    private ImageData data;
    private boolean noCache=true;

    public ApiImagePreview(ImageData data) {
        super(data);
    }

    @Override
    public void processView(ServletContext context, ApiRequestData rdata, HttpServletResponse response) {
        process(context,rdata,response);
    }

}
