package com.sdky.faceidentify.utils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

import static com.sdky.faceidentify.core.Conts.API_KEY;
import static com.sdky.faceidentify.core.Conts.SECRET_KEY;
import static com.sdky.faceidentify.core.Conts.URL_ACCESS_TOKEN;

/**
 * http 工具类
 */
public class HttpUtil {

    public static void post(final String requestUrl, final String params, final HttpCallBack callBack) throws Exception {

        HttpCallBack access = new HttpCallBack() {
            @Override
            public void getResult(Object o) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    String access_token = jsonObject.getString("access_token");
                    String generalUrl = requestUrl + "?access_token=" + access_token;
                    OkGo.post(generalUrl).upString(params).execute(new StringCallback(){
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            callBack.getResult(s);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            callBack.getError(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.getError(e.getMessage());
                } finally {

                }
            }

            @Override
            public void getError(Object o) {
                callBack.getError(o);
            }
        };

        getAccessToken(access);
    }

    public static void getAccessToken(final HttpCallBack callBack) {
        String getAccessTokenUrl = URL_ACCESS_TOKEN
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + API_KEY
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + SECRET_KEY;
        try {
            OkGo.post(getAccessTokenUrl).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    callBack.getResult(s);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    callBack.getError(e.getMessage());
                }
            });
        } catch (Exception e){

        }
    }
}
