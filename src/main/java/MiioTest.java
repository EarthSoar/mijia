import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import utils.RandomUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

public class MiioTest {

    private static String deviceId = "";
    private static String userId = "";
    private static String serviceToken = "";
    private static String securityToken = "";

    public static void main(String[] args) throws Exception {

        // 登录
        // 请将返回的deviceId、userId、serviceToken、securityToken填入上面静态变量中
        // 登录一次即可，如果token失效再去登录
        System.out.println(LoginService.login("xiaomiio", "账号", "密码"));

        // 参数说明
        // did: 设备ID
        // siid: 功能分类ID
        // piid: 设备属性ID
        // aiid: 设备方法ID

        // 米家产品库
        // https://home.miot-spec.com/


        // 获取全部设备列表
        // 返回结果说明
        // name: 设备名称
        // did: 设备ID
        // isOnline: 设备是否在线
        // model: 设备产品型号, 根据这个去米家产品库查该产品相关的信息
        //post("/home/device_list", "{\"getVirtualModel\": false, \"getHuamiDevices\": 0}");

        // 获取设备属性(例如获取风扇的开关机状态和风速)
        //post("/miotspec/prop/get", "{\"params\":[{\"did\":\"111111111\",\"siid\":2,\"piid\":1},{\"did\":\"111111111\",\"siid\":2,\"piid\":6}]}");

        // 设置设备属性(例如风扇开机并设置风速为70)
        //post("/miotspec/prop/set", "{\"params\":[{\"did\":\"111111111\",\"siid\":2,\"piid\":1,\"value\":true},{\"did\":\"111111111\",\"siid\":2,\"piid\":6,\"value\":70}]}");

        // 调用设备方法(例如让小爱音箱朗读指定文本)
        // in: 入参
        // 例如小爱音箱pro的执行指令方法参数为
        // 1 - text-content 指令文本
        // 2 - silent-execution 是否静默执行
        //post("/miotspec/action", "{\"params\":{\"did\":\"111111111\",\"siid\":5,\"aiid\":5,\"in\":[\"开灯\", true]}}");

        // 获取房间列表
        //post("/v2/homeroom/gethome", "{\"fg\":false,\"fetch_share\":true,\"fetch_share_dev\":true,\"limit\":300,\"app_ver\":7}");

        // 获取设备耗材(home_id可从上面的获取房间列表接口得知, owner_id即userId)
        //post("/v2/home/standard_consumable_items", "{\"home_id\":111111111,\"owner_id\":111111111}");

        // 获取红外遥控器的按键列表
        //post("/v2/irdevice/controller/keys", "{\"did\":\"ir.111111111\"}");

        // 触发红外遥控器按键
        //post("/v2/irdevice/controller/key/click", "{\"did\": \"ir.111111111\", \"key_id\": 100000001}");

        // 获取场景列表(包含手动场景和自动化)
        //post("/appgateway/miot/appsceneservice/AppSceneService/GetSceneList", "{\"home_id\":\"111111111\"}");

        // 执行手动场景
        //post("/appgateway/miot/appsceneservice/AppSceneService/RunScene", "{\"scene_id\":\"111111111\",\"trigger_key\":\"user.click\"}");

    }

    public static String generateNonce() {
        return RandomUtil.random(16);
    }

    public static String generateSignedNonce(String secret, String nonce) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(Base64.getDecoder().decode(secret));
        messageDigest.update(Base64.getDecoder().decode(nonce));
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

    public static String generateSignature(String url, String signedNonce, String nonce, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        String sign = url+"&"+signedNonce+"&"+nonce+"&data="+data;
        hmac.init(new SecretKeySpec(Base64.getDecoder().decode(signedNonce), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(hmac.doFinal(sign.getBytes("utf-8")));
    }

    // 调用接口
    public static void post(String uri, String data) {
        try {
            if (serviceToken == null) {
                System.out.println("请先登录");
                return;
            }

            OkHttpClient client = new OkHttpClient();

            String nonce = generateNonce();
            String signedNonce = generateSignedNonce(securityToken, nonce);
            String signature = generateSignature(uri, signedNonce, nonce, data);

            FormBody body = new FormBody.Builder()
                    .add("_nonce", nonce)
                    .add("data", data)
                    .add("signature", signature)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.io.mi.com/app" + uri)
                    .post(body)
                    .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                    .header("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
                    .header("Cookie", "PassportDeviceId="+deviceId+";userId="+userId+";serviceToken="+serviceToken+";")
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject result = JSONObject.parseObject(response.body().string());
                    System.out.println(response.code());
                    System.out.println(result);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
