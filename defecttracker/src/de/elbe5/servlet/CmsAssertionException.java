package de.elbe5.servlet;

import de.elbe5.request.ResponseCode;

public class CmsAssertionException extends CmsException{

    public CmsAssertionException(){
        super(ResponseCode.CONFLICT);
    }

    public CmsAssertionException(String message){
        super(ResponseCode.CONFLICT, message);
    }

}
