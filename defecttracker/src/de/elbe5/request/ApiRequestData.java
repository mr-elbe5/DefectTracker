package de.elbe5.request;

import de.elbe5.application.Configuration;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

public class ApiRequestData extends RequestData {

    private String token;
    private UserData user=null;

    public static ApiRequestData getRequestData(HttpServletRequest request) {
        return (ApiRequestData) request.getAttribute(KEY_REQUESTDATA);
    }

    public ApiRequestData(String method, HttpServletRequest request) {
        super(method, request);
        token = request.getHeader("Authentication");
        if (token==null || token.isEmpty())
            token = request.getHeader("token");
        if (token==null)
            token="";
    }

    public void tryLogin(){
        if (token.isEmpty())
            return;
        user = UserBean.getInstance().loginUserByToken(token);
    }

    @Override
    public UserData getLoginUser() {
        return user;
    }

    @Override
    public Locale getLocale() {
        return Configuration.getDefaultLocale();
    }

}
