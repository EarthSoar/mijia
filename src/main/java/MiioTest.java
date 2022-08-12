import com.alibaba.fastjson.JSONObject;
import javafx.util.Duration;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RandomUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MiioTest {
    private static final Logger logger = LoggerFactory.getLogger(MiioTest.class);

    private static String deviceId = "Xs7Qu1Kw7OhpZsc5";
    private static String userId = "2552697296";
    private static String serviceToken = "tRXpOIcyevL6Lx2I+VAK+VkMyaJxX9HKXuk+RPjH53ZLpuGsqJXI/BIALr0XX4qDvOPzE2yHb+ytEVjwV3ymPGFMXK90MR/yOY0rJoqWz3kxcC2JIln7iKwt8nFm8hv4kBIKDfk3+5JcVlHGNhj7k4GcqvUepmP/jTAnO4G01Es=";
    private static String securityToken = "u1/jA8EmRt/oMYDwTOyRDA==";
    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);

    private static long lastAlterTime;

    public static void token() throws Exception {
        JSONObject json = LoginService.login("xiaomiio", "", "+");
        logger.info(String.format("getToken: %s", json));

        pool.scheduleAtFixedRate(() -> {
            try {
                securityToken = json.getString("securityToken");
                deviceId = json.getString("deviceId");
                userId = json.getString("userId");
                serviceToken = json.getString("serviceToken");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }


    public static void monitor() {
        pool.scheduleAtFixedRate(() -> {
            JSONObject res = post("/home/device_list", "{\"getVirtualModel\": false, \"getHuamiDevices\": 0}");
            logger.info(String.format("getDeviceList: %s", res));
            if (res == null) {
                return;
            }
            System.out.println(res);
            Result result = JSONObject.parseObject(res.toString(), Result.class);
            if (result == null || result.code != 0 || result.result == null || result.result.list == null || result.result.list.size() == 0) {
                return;
            }
            Item item = result.result.list.get(0);
            if (!item.isOnline) {
                if ((System.currentTimeMillis() - lastAlterTime) / (1000 * 60) > 120) {
                    logger.error("告警~");
                    Alter();
                    lastAlterTime = System.currentTimeMillis();
                }
            }
        }, 0, 2, TimeUnit.MINUTES);
    }


    public static void main(String[] args) throws Exception {

        token();
        monitor();




        for (;;){

        }

        // 获取全部设备列表
        // 返回结果说明
        // name: 设备名称
        // did: 设备ID
        // isOnline: 设备是否在线
        // model: 设备产品型号, 根据这个去米家产品库查该产品相关的信息




        //{"cUserId":"gNG7BN6rKj8nSw8qeWFRv2x4Zj0","securityToken":"u1/jA8EmRt/oMYDwTOyRDA==","code":0,"serviceToken":"tRXpOIcyevL6Lx2I+VAK+VkMyaJxX9HKXuk+RPjH53ZLpuGsqJXI/BIALr0XX4qDvOPzE2yHb+ytEVjwV3ymPGFMXK90MR/yOY0rJoqWz3kxcC2JIln7iKwt8nFm8hv4kBIKDfk3+5JcVlHGNhj7k4GcqvUepmP/jTAnO4G01Es=","message":"登录成功","userId":"2552697296","deviceId":"Xs7Qu1Kw7OhpZsc5"}
        // 登录
        // 请将返回的deviceId、userId、serviceToken、securityToken填入上面静态变量中
        // 登录一次即可，如果token失效再去登录
//        System.out.println(LoginService.login("xiaomiio", "13572377245", "a15336125311+"));

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
//        post("/home/device_list", "{\"getVirtualModel\": false, \"getHuamiDevices\": 0}");

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
    public static JSONObject post(String uri, String data) {
        try {
            if (serviceToken == null) {
                System.out.println("请先登录");
                return null;
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
            Response r = call.execute();
            if (r.code() == 200) {
                JSONObject result = JSONObject.parseObject(r.body().string());
                return result;
            } else {
                System.out.println("请求失败");
                return null;
            }

//
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    JSONObject result = JSONObject.parseObject(response.body().string());
//                    System.out.println(response.code());
//                    System.out.println(result);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    ///参数	必须	备注
    //app	必须	需要告警集成的应用KEY
    //eventType	必须	触发告警trigger，解决告警resolve
    //eventId	可选	外部事件id，告警关闭时用到
    //alarmName	可选	告警标题，alarmName与eventId不能同事为空
    //alarmContent	必须	告警内容详情
    //entityName	可选	告警对象名
    //entityId	可选	告警对象id
    //priority	可选	提醒 1，警告 2，严重 3，通知 4，致命 5
    //host	可选	主机
    //service	可选	服务
    //details	可选	详情
    //contexts	可选	上下文
    public static void Alter() {
        try {
            OkHttpClient client = new OkHttpClient();


            FormBody body = new FormBody.Builder()
                    .add("app", "")
                    .add("eventId", String.valueOf(System.currentTimeMillis()))
                    .add("eventType", "trigger")
                    .add("priority", "2")
                    .add("alarmContent", "tingdian")
                    .add("alarmName", "停电了")
                    .build();

            Request request = new Request.Builder()
                    .url("http://api.aiops.com/alert/api/event")
                    .post(body)
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



    class Result {
        int code;
        String message;
        Data result;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getResult() {
            return result;
        }

        public void setResult(Data result) {
            this.result = result;
        }
    }
    class Item {
        String name;
        boolean isOnline;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean online) {
            isOnline = online;
        }
    }
    class Data {
        List<Item> list;

        public List<Item> getList() {
            return list;
        }

        public void setList(List<Item> list) {
            this.list = list;
        }
    }

}
