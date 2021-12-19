package de.elbe5.request;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.cache.Strings;
import de.elbe5.application.Configuration;
import de.elbe5.content.ContentData;
import de.elbe5.user.UserData;

import javax.servlet.http.*;
import java.util.*;

public class SessionRequestData extends RequestData {

    public static SessionRequestData getRequestData(HttpServletRequest request) {
        return (SessionRequestData) request.getAttribute(KEY_REQUESTDATA);
    }

    private HttpSession session;
    private Map<String, Cookie> cookies = new HashMap<>();

    public SessionRequestData(String method, HttpServletRequest request) {
        super(method, request);
    }

    /*********** message *********/

    public boolean hasMessage() {
        return containsKey(KEY_MESSAGE);
    }

    public void setMessage(String msg, String type) {
        put(KEY_MESSAGE, msg);
        put(KEY_MESSAGETYPE, type);
    }

    /************ user ****************/

    @Override
    public UserData getLoginUser() {
        return getSessionUser();
    }

    @Override
    public Locale getLocale() {
        return getSessionLocale();
    }

    /************ form error *************/

    public boolean checkFormErrors() {
        if (formError == null)
            return true;
        if (formError.isFormIncomplete())
            formError.addFormError(Strings.string("_notComplete", getLocale()));
        return formError.isEmpty();
    }

    /************** request attributes ***************/

    public void setRequestObject(String key, Object obj){
        request.setAttribute(key, obj);
    }

    public Object getRequestObject(String key){
        return request.getAttribute(key);
    }

    public <T> T getRequestObject(String key, Class<T> cls) {
        try {
            return cls.cast(request.getAttribute(key));
        }
        catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void removeRequestObject(String key){
        request.removeAttribute(key);
    }

    /************** session attributes ***************/

    public void initSession() {
        session = request.getSession(true);
        if (session.isNew()) {
            Locale requestLocale = request.getLocale();
            if (Configuration.hasLanguage(requestLocale))
                setSessionLocale(requestLocale);
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String host = url.substring(0, url.indexOf(uri));
            setSessionHost(host);
        }
    }

    public void setSessionObject(String key, Object obj) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.setAttribute(key, obj);
    }

    public Object getSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        return session.getAttribute(key);
    }

    private void removeAllSessionObjects(){
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        Enumeration<String>  keys = session.getAttributeNames();
        while (keys.hasMoreElements()){
            String key=keys.nextElement();
            session.removeAttribute(key);
        }
    }

    public <T> T getSessionObject(String key, Class<T> cls) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        try {
            return cls.cast(request.getSession().getAttribute(key));
        }
        catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void removeSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.removeAttribute(key);
    }

    public ClipboardData getClipboard() {
        ClipboardData data = getSessionObject(KEY_CLIPBOARD,ClipboardData.class);
        if (data==null){
            data=new ClipboardData();
            setSessionObject(KEY_CLIPBOARD,data);
        }
        return data;
    }

    public void setClipboardData(String key, BaseData data){
        getClipboard().putData(key,data);
    }

    public boolean hasClipboardData(String key){
        return getClipboard().hasData(key);
    }

    public <T> T getClipboardData(String key,Class<T> cls) {
        try {
            return cls.cast(getClipboard().getData(key));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void clearClipboardData(String key){
        getClipboard().clearData(key);
    }

    public void setSessionUser(UserData data) {
        setSessionObject(KEY_LOGIN, data);
    }

    public UserData getSessionUser() {
        return (UserData) getSessionObject(KEY_LOGIN);
    }

    public void setCurrentRequestContent(ContentData data) {
        //Log.log("set current request content: " + data.getClass().getSimpleName());
        setRequestObject(KEY_CONTENT, data);
    }

    public ContentData getCurrentContent() {
        return getCurrentContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentContent(Class<T> cls) {
        try {
            Object obj=getRequestObject(KEY_CONTENT);
            if (obj==null)
                obj=getSessionObject(KEY_CONTENT);
            assert(obj!=null);
            //Log.log("current request content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException e){
            return null;
        }
    }

    public void setCurrentSessionContent(ContentData data) {
        //Log.log("set current session content: " + data.getClass().getSimpleName());
        setSessionObject(KEY_CONTENT, data);
    }

    public void removeCurrentSessionContent() {
        removeSessionObject(KEY_CONTENT);
    }

    public ContentData getCurrentSessionContent() {
        return getCurrentSessionContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentSessionContent(Class<T> cls) {
        try {
            Object obj=getSessionObject(KEY_CONTENT);
            //Log.log("current session content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException e){
            return null;
        }
    }

    public void setSessionLocale() {
        setSessionLocale(Configuration.getDefaultLocale());
    }

    public Locale getSessionLocale() {
        Locale locale = getSessionObject(KEY_LOCALE,Locale.class);
        if (locale == null) {
            return Configuration.getDefaultLocale();
        }
        return locale;
    }

    public void setSessionLocale(Locale locale) {
        if (Configuration.hasLanguage(locale)) {
            setSessionObject(KEY_LOCALE, locale);
        } else {
            setSessionObject(KEY_LOCALE, Configuration.getDefaultLocale());
        }
    }

    public void setSessionHost(String host) {
        setSessionObject(KEY_HOST, host);
    }

    public String getSessionHost() {
        return getSessionObject(KEY_HOST,String.class);
    }

    public void resetSession() {
        Locale locale = getLocale();
        removeAllSessionObjects();
        request.getSession(true);
        setSessionLocale(locale);
    }

    /*************** cookie methods ***************/

    public void addLoginCookie(String name, String value, int expirationDays){
        Cookie cookie=new Cookie("elbe5cms_"+name,value);
        cookie.setPath("/ctrl/user/login");
        cookie.setMaxAge(expirationDays*24*60*60);
        cookies.put(cookie.getName(),cookie);
    }

    public boolean hasCookies(){
        return !(cookies.isEmpty());
    }

    public void setCookies(HttpServletResponse response){
        for (Cookie cookie : cookies.values()){
            response.addCookie(cookie);
        }
    }

    public Map<String,String> readLoginCookies(){
        Map<String, String> map=new HashMap<>();
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().startsWith("elbe5cms_")) {
                map.put(cookie.getName().substring(9), cookie.getValue());
            }
        }
        return map;
    }

}
