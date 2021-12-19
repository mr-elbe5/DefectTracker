package de.elbe5.servlet;

public abstract class ApiController {

    public abstract String getKey();

    protected void checkRights(boolean hasRights){
        if (!hasRights)
            throw new CmsAuthorizationException();
    }

}
