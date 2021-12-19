package de.elbe5.request;

import de.elbe5.application.Configuration;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.KeyValueMap;
import de.elbe5.base.json.JsonDeserializer;
import de.elbe5.base.log.Log;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserData;
import org.json.simple.JSONObject;

import javax.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class RequestData extends KeyValueMap {

    public static final String KEY_REQUESTDATA = "$REQUESTDATA";
    public static final String KEY_URL = "$URL";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_HOST = "$HOST";
    public static final String KEY_JSP = "$JSP";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGETYPE = "$MESSAGETYPE";
    public static final String KEY_TARGETID = "$TARGETID";
    public static final String KEY_CLIPBOARD = "$CLIPBOARD";
    public static final String KEY_TITLE = "$TITLE";
    public static final String KEY_LOGIN = "$LOGIN";
    public static final String KEY_CAPTCHA = "$CAPTCHA";
    public static final String KEY_CONTENT = "contentData";
    public static final String KEY_DOCUMENT = "documentData";
    public static final String KEY_IMAGE = "imageData";

    public static final String MESSAGE_TYPE_INFO = "info";
    public static final String MESSAGE_TYPE_SUCCESS = "success";
    public static final String MESSAGE_TYPE_ERROR = "danger";


    public static RequestData getRequestData(HttpServletRequest request) {
        return (RequestData) request.getAttribute(KEY_REQUESTDATA);
    }

    protected HttpServletRequest request;

    private int id = 0;

    private String method;

    protected FormError formError = null;

    public RequestData(String method, HttpServletRequest request) {
        this.request = request;
        this.method = method;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract UserData getLoginUser();

    public abstract Locale getLocale();

    public boolean isPostback() {
        return method.equals("POST");
    }

    /************ user ****************/

    public int getUserId() {
        UserData user = getLoginUser();
        return user == null ? 0 : user.getId();
    }

    public boolean isLoggedIn() {
        UserData user = getLoginUser();
        return user != null;
    }

    public boolean hasAnySystemRight() {
        UserData data = getLoginUser();
        return data != null && (data.hasAnySystemRight());
    }

    public boolean hasAnyElevatedSystemRight() {
        UserData data = getLoginUser();
        return data != null && (data.hasAnyElevatedSystemRight());
    }

    public boolean hasAnyContentRight() {
        UserData data = getLoginUser();
        return data != null && (data.isRoot() || data.hasAnyContentRight());
    }

    public boolean hasSystemRight(SystemZone zone) {
        UserData data = getLoginUser();
        return data != null && (data.isRoot() || data.hasSystemRight(zone));
    }

    /************ form error *************/

    public FormError getFormError(boolean create) {
        if (formError == null && create)
            formError = new FormError();
        return formError;
    }

    public void addFormError(String s) {
        getFormError(true).addFormError(s);
    }

    public void addFormField(String field) {
        getFormError(true).addFormField(field);
    }

    public void addIncompleteField(String field) {
        getFormError(true).addFormField(field);
        getFormError(false).setFormIncomplete();
    }

    public boolean hasFormError() {
        return formError != null && !formError.isEmpty();
    }

    public boolean hasFormErrorField(String name) {
        if (formError == null)
            return false;
        return formError.hasFormErrorField(name);
    }

    /************** request attributes *****************/

    public void readRequestParams() {
        if (isPostback()) {
            String type = request.getContentType();
            if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
                getMultiPartParams();
            } else if (type != null && type.toLowerCase().equals("application/octet-stream")) {
                getSinglePartParams();
                getByteStream();
            } else if (type != null && type.toLowerCase().equals("application/json")) {
                getSinglePartParams();
                getJsonStream();
            } else {
                getSinglePartParams();
            }
        } else {
            getSinglePartParams();
        }
    }

    private void getByteStream(){
        try {
            InputStream in = request.getInputStream();
            BinaryFile file=new BinaryFile();
            file.setBytesFromStream(in);
            file.setFileSize(file.getBytes().length);
            file.setFileName(request.getHeader("fileName"));
            file.setContentType(request.getHeader("contentType"));
            put("file", file);
        }
        catch (IOException ioe){
            Log.error("input stream error", ioe);
        }
    }

    private void getJsonStream(){
        try {
            InputStream in = request.getInputStream();
            try {
                JSONObject json = (JSONObject) new JsonDeserializer().deserialize(in);
                //Log.log("received json: "+ json.toJSONString());
                for (Object key : json.keySet()){
                    put(key.toString(), json.get(key));
                }
            }
            catch (Exception e){
                Log.error("unable to get params from json");
            }
            in.close();
        }
        catch (IOException ioe){
            Log.error("json input stream error", ioe);
        }
    }

    private void getSinglePartParams() {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            put(key, strings);
        }
    }

    private void getMultiPartParams() {
        Map<String, List<String>> params = new HashMap<>();
        Map<String, List<BinaryFile>> fileParams = new HashMap<>();
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName = getFileName(part);
                if (fileName != null) {
                    if (fileName.isEmpty())
                        continue;
                    BinaryFile file = getMultiPartFile(part, fileName);
                    if (file != null) {
                        List<BinaryFile> values;
                        if (fileParams.containsKey(name))
                            values = fileParams.get(name);
                        else {
                            values = new ArrayList<>();
                            fileParams.put(name, values);
                        }
                        values.add(file);
                    }
                } else {
                    String param = getMultiPartParameter(part);
                    if (param != null) {
                        List<String> values;
                        if (params.containsKey(name))
                            values = params.get(name);
                        else {
                            values = new ArrayList<>();
                            params.put(name, values);
                        }
                        values.add(param);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("error while parsing multipart params", e);
        }
        for (String key : params.keySet()) {
            List<String> list = params.get(key);
            if (list.size() == 1) {
                put(key, list.get(0));
            } else {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strings[i] = list.get(i);
                }
                put(key, strings);
            }
        }
        for (String key : fileParams.keySet()) {
            List<BinaryFile> list = fileParams.get(key);
            if (list.size() == 1) {
                put(key, list.get(0));
            } else {
                BinaryFile[] files = new BinaryFile[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    files[i] = list.get(i);
                }
                put(key, files);
            }
        }
    }

    private String getMultiPartParameter(Part part) {
        try {
            byte[] bytes = new byte[(int) part.getSize()];
            int read = part.getInputStream().read(bytes);
            if (read > 0) {
                return new String(bytes, Configuration.ENCODING);
            }
        } catch (Exception e) {
            Log.error("could not extract parameter from multipart", e);
        }
        return null;
    }

    private BinaryFile getMultiPartFile(Part part, String fileName) {
        try {
            BinaryFile file = new BinaryFile();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setFileSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(file.getFileSize());
            byte[] buffer = new byte[8096];
            int len;
            while ((len = in.read(buffer, 0, 8096)) != -1) {
                out.write(buffer, 0, len);
            }
            file.setBytes(out.toByteArray());
            return file;
        } catch (Exception e) {
            Log.error("could not extract file from multipart", e);
            return null;
        }
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
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

}
