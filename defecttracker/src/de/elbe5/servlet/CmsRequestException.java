package de.elbe5.servlet;

import de.elbe5.request.ResponseCode;

public class CmsRequestException extends CmsException{

    public CmsRequestException(){
        super(ResponseCode.BAD_REQUEST);
    }

}
