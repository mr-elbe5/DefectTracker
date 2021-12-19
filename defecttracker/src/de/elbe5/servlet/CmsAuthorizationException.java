package de.elbe5.servlet;

import de.elbe5.request.ResponseCode;

public class CmsAuthorizationException extends CmsException{

    public CmsAuthorizationException(){
        super(ResponseCode.UNAUTHORIZED);
    }

}
