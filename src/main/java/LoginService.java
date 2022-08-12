import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import utils.MD5Util;
import utils.RandomUtil;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

public class LoginService {

    // 登录
    public static JSONObject login(String sid, String user, String pwd) throws Exception {
        JSONObject data = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        Response response = null;
        JSONObject result = null;

        // ----------------------------------------------------
        request = new Request.Builder()
                .url("https://account.xiaomi.com/pass/serviceLogin?sid=" + sid + "&_json=true")
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .get()
                .build();
        response = client.newCall(request).execute();
        result = JSONObject.parseObject(response.body().string().substring(11));

        // ----------------------------------------------------
        FormBody body = new FormBody.Builder()
                .add("qs", result.getString("qs"))
                .add("sid", result.getString("sid"))
                .add("_sign", result.getString("_sign"))
                .add("callback", result.getString("callback"))
                .add("user", user)
                .add("hash", MD5Util.encrypt(pwd).toUpperCase())
                .add("_json", "true")
                .build();
        request = new Request.Builder()
                .url("https://account.xiaomi.com/pass/serviceLoginAuth2")
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .post(body)
                .build();
        response = client.newCall(request).execute();
        result = JSONObject.parseObject(response.body().string().substring(11));
        if (result.getIntValue("code") != 0) {
            data.put("code", result.getIntValue("code"));
            data.put("message", result.getString("desc"));
            return data;
        }
        String nonce = result.getString("nonce");
        String location = result.getString("location");
        String userId = result.getString("userId");
        String securityToken = result.getString("ssecurity");

        // ----------------------------------------------------
        String n = "nonce=" + nonce + "&" + securityToken;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        messageDigest.update(n.getBytes("utf-8"));
        request = new Request.Builder()
                .url(location + "&clientSign=" + URLEncoder.encode(Base64.getEncoder().encodeToString(messageDigest.digest()), "utf-8"))
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .get()
                .build();
        response = client.newCall(request).execute();
        List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
        for (Cookie cookie : cookies) {
            data.put(cookie.name(), cookie.value());
        }
        data.put("userId", userId);
        data.put("securityToken", securityToken);
        data.put("deviceId", RandomUtil.random(16));
        data.put("code", 0);
        data.put("message", "登录成功");
        return data;
    }

}
