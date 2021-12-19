package de.elbe5.user;

import de.elbe5.base.log.Log;
import de.elbe5.base.util.DateUtil;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.servlet.ApiController;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.view.ApiResponseCodeView;
import de.elbe5.view.IApiView;
import de.elbe5.view.JsonView;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

public class UserApiController extends ApiController {

    public static final String KEY = "user";

    private static UserApiController instance = null;

    public static void setInstance(UserApiController instance) {
        UserApiController.instance = instance;
    }

    public static UserApiController getInstance() {
        return instance;
    }

    public static void register(UserApiController controller){
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @SuppressWarnings("unchecked")
    public IApiView login(ApiRequestData rdata) {
        Log.log("try login");
        checkRights(rdata.isPostback());
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        }
        UserData data = UserBean.getInstance().loginApiUser(login, pwd);
        if (data == null) {
            Log.info("bad login of "+login);
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        }

        LocalDateTime expiration = getExpiration();
        if (data.getToken().isEmpty()){
            if (!UserBean.getInstance().setToken(data, expiration))
                return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        }
        else {
            if (!UserBean.getInstance().updateToken(data, expiration))
                return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        }
        JSONObject json = new JSONObject();
        json.put("id",data.getId());
        json.put("login",data.getLogin());
        json.put("name", data.getName());
        json.put("token", data.getToken());
        json.put("expiration", DateUtil.asMillis(data.getTokenExpiration()));
        Log.log(json.toJSONString());
        return new JsonView(json.toJSONString());
    }

    public IApiView checkTokenLogin(ApiRequestData rdata) {
        checkRights(rdata.isPostback());
        UserData data=rdata.getLoginUser();
        if (data==null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        return new ApiResponseCodeView(ResponseCode.OK);
    }

    private LocalDateTime getExpiration(){
        return LocalDateTime.of(2050, 01, 01, 0, 0 );
    }

}
