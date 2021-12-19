package de.elbe5.view;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.request.ApiRequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ApiBinaryFileView extends BinaryFileView implements IApiView{

    public ApiBinaryFileView(BinaryFile file) {
        super(file);
    }

    @Override
    public void processView(ServletContext context, ApiRequestData rdata, HttpServletResponse response) {
        process(context, rdata,response);
    }

}
