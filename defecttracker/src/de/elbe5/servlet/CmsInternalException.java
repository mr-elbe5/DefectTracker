package de.elbe5.servlet;

import de.elbe5.request.ResponseCode;

public class CmsInternalException extends CmsException{

    public CmsInternalException(){
        super(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    public CmsInternalException(String message){
        super(ResponseCode.INTERNAL_SERVER_ERROR, message);
    }

}
