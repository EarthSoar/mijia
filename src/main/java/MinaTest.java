import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import utils.RandomUtil;

import java.io.IOException;
import java.util.Map;

public class MinaTest {

    private static String userId = "";
    private static String serviceToken = "";

    public static void main(String[] args) throws Exception {
        // 登录
        // 请将返回的userId、serviceToken填入上面静态变量中
        // 登录一次即可，如果token失效再去登录
        //System.out.println(LoginService.login("micoapi", "账号", "密码"));

        //=== 音箱列表
        //get("/admin/v2/device_list");

        //=== 获取播放状态
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_get_play_status\", \"message\": {}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 歌曲信息
        //post("/aivs3/audio/info/v2", JSONObject.parseObject("{\"audioIdList\": [\"1145011744351977621\",\"499104550126418\",\"1117873106224940067\"]}"));

        //=== 播放
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_play_operation\", \"message\": {\"action\":\"play\"}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 暂停
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_play_operation\", \"message\": {\"action\":\"pause\"}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 上一首
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_play_operation\", \"message\": {\"action\":\"prev\"}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 下一首
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_play_operation\", \"message\": {\"action\":\"next\"}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 跳转时间
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_set_positon\", \"message\": {\"media\":\"app_android\",\"position\":1000}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 播放(播放列表里)第几首歌
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_play_index\", \"message\": {\"media\":\"app_android\",\"index\":2}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 循环模式: 0=单曲，1=顺序，3=随机
        //post("/remote/ubus", JSONObject.parseObject("{\"path\": \"mediaplayer\", \"method\": \"player_set_loop\", \"message\": {\"media\":\"app_android\",\"type\":1}, \"deviceId\": \"11111111-1111-1111-1111-111111111111\"}"));

        //=== 获取我的歌单列表, 记住【name=我喜欢】的歌单ID, 用于添加到我喜欢
        //get("/music/playlist/v2/lists");

        //=== 添加到我喜欢, listId=我喜欢的歌单ID
        //post("/aivs3/audio/collect", JSONObject.parseObject("{\"audioId\": \"479102204545583\", \"listId\": \"111111111111111111\"}"));
        //=== 取消我喜欢, listId=我喜欢的歌单ID
        //post("/aivs3/audio/cancel_collect", JSONObject.parseObject("{\"audioId\": \"479102204545583\", \"listId\": \"111111111111111111\"}"));

        //=== 歌单里的歌曲列表
        // 我喜欢 /music/playlist/v2/songs?listId=我喜欢的歌单ID&offset=0&count=20
        // 热歌榜 /music/playlist/v2/songs?listId=111111111111126&offset=0&count=20
        // 新歌榜 /music/playlist/v2/songs?listId=111111111111127&offset=0&count=20
        // 飙升榜 /music/playlist/v2/songs?listId=111111111111162&offset=0&count=20
        //get("/music/playlist/v2/songs?listId=111111111111126&offset=0&count=20");

        //=== 电台里的歌曲列表(每次请求随机歌曲)
        // 华语 id=118
        // 粤语 id=119
        // 英语 id=120
        // ... 其他电台id自行抓包
        //get("/apphomepage/patchwall/appv2/songbook?id=118&type=radio");

        //=== 歌词, 传的是songId，不是audioId
        //get("/music/qq/lyric?originSongId=313880055");

        //=== 搜索, queryType: 1音乐 4电台
        //post("/music/search", JSONObject.parseObject("{\"query\": \"漠河舞厅\", \"queryType\": 1, \"offset\": 0, \"count\": 20, \"supportPayment\": true}"));


        //=== 播放音乐
        // 从上面的几个接口拿到歌单或电台里的音乐，或者搜索歌曲后，调用此接口进行播放
        // 消息结构太过复杂, 这里贴出前端代码
        // dialog_id: "app_android_'+随机20位字符串
        // audio_items: 要播放的歌曲列表
        // startOffset: 从第几首开始播放
        // startaudioid: 开始播放的音频ID
        // 该接口好像只支持REPLACE_ALL, 即替换当前播放列表
        // 如果要往播放列表里插入一首歌，就先获取当前播放列表，加上要播放的歌曲，重新构建消息结构

//        playMusic(list, startOffset) {
//            let audio_items = '';
//            list.forEach((song)=>{
//                    audio_items += '{\\"item_id\\":{\\"audio_id\\":\\"'+song.audioId+'\\",\\"cp\\":{\\"album_id\\":\\"-1\\",\\"episode_index\\":0,\\"id\\":\\"'+song.songId+'\\",\\"name\\":\\"xiaowei\\"}},\\"stream\\":{\\"authentication\\":true,\\"duration_in_ms\\":'+song.duration+',\\"offset_in_ms\\":0,\\"redirect\\":false,\\"url\\":\\"'+song.url+'\\"}},';
//				});
//            let message = '{"dialog_id":"app_android_'+this.random(20)+'","loadMoreOffset":'+list.length+',"media":"app_android","music":"{\\"payload\\":{\\"audio_items\\":['+audio_items.substring(0, audio_items.length-1)+'],\\"audio_type\\":\\"MUSIC\\",\\"list_params\\":{\\"listId\\":\\"-1\\",\\"loadmore_offset\\":'+list.length+',\\"origin\\":\\"xiaowei\\",\\"type\\":\\"SONGBOOK\\"},\\"needs_loadmore\\":false,\\"play_behavior\\":\\"REPLACE_ALL\\"}}","startOffset":'+startOffset+',"startaudioid":"'+list[startOffset].audioId+'"}';
//            mina.post({
//                    uri: '/remote/ubus',
//                    data: {
//                        path: 'mediaplayer',
//                        method: 'player_play_music',
//                        message: message,
//                        deviceId: this.deviceId
//                    }
//				}, (r) => {
//                this.loadSong();
//            });
//        }

    }

    public static void get(String uri) {
        try {
            if (serviceToken == null) {
                System.out.println("请先登录");
                return;
            }

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api2.mina.mi.com" + uri)
                    .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                    .header("Cookie", "userId="+userId+";serviceToken="+serviceToken+";")
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //JSONObject result = JsonObject.parseObject(response.body().string());
                    // 注：
                    // mina接口返回的audioId(音频ID)为Long类型，并未包装为String类型
                    // 如果返回JSONObject对象给uniapp，则会丢失精度，导致uniapp收到的audioId不准确
                    // 这里建议直接返回response.body().string()，即返回字符串给uniapp
                    // 然后uniapp使用json-bigint库进行解析，json-bigint会将Long数据包装为String，避免精度丢失
                    // json-bigint使用方法：
                    // import JSONbig from 'json-bigint';
                    // JSONbig.parse(r);
                    // 如果不是使用uniapp开发前端，而是使用安卓原生开发的话可忽略

                    System.out.println(response.code());
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String uri, JSONObject data) {
        try {
            if (serviceToken == null) {
                System.out.println("请先登录");
                return;
            }

            OkHttpClient client = new OkHttpClient();

            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
            builder.add("requestId", RandomUtil.random(20));

            RequestBody body = builder.build();

            Request request = new Request.Builder()
                    .url("https://api2.mina.mi.com" + uri)
                    .post(body)
                    .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                    .header("Cookie", "userId="+userId+";serviceToken="+serviceToken+";")
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //JSONObject result = JsonObject.parseObject(response.body().string());
                    // 注：
                    // mina接口返回的audioId(音频ID)为Long类型，并未包装为String类型
                    // 如果返回JSONObject对象给uniapp，则会丢失精度，导致uniapp收到的audioId不准确
                    // 这里建议直接返回response.body().string()，即返回字符串给uniapp
                    // 然后uniapp使用json-bigint库进行解析，json-bigint会将Long数据包装为String，避免精度丢失
                    // json-bigint使用方法：
                    // import JSONbig from 'json-bigint';
                    // JSONbig.parse(r);
                    // 如果不是使用uniapp开发前端，而是使用安卓原生开发的话可忽略

                    System.out.println(response.code());
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
