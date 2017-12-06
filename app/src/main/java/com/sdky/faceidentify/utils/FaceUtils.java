package com.sdky.faceidentify.utils;

import android.util.Log;

import com.sdky.faceidentify.bean.UserBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import static com.sdky.faceidentify.core.Conts.URL_ADD;
import static com.sdky.faceidentify.core.Conts.URL_IDENTIFY;

/**
 * Created by liurui on 2017/4/24.
 */

public class FaceUtils {
    public static UserBean addUser(final UserBean user) {
        try {
            byte[] imgData1 = FileUtil.readFileByBytes(user.getImages());
            String imgStr1 = Base64Util.encode(imgData1);

            String params = "uid=" + user.getUser_id() + "&user_info=" + user.getUser_info() + "&group_id=" + user.getGroup_id() + "&"
                    + URLEncoder.encode("images", "UTF-8") + "=" + URLEncoder.encode(imgStr1, "UTF-8");
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            HttpUtil.post(URL_ADD, params, new HttpCallBack() {
                @Override
                public void getResult(Object o) {
                    String s = (String) o;
                    Log.d("TAG", "result = " + s);
                    JSONObject object = null;
                    try {
                        object = new JSONObject(s);
                        user.setLog_id(object.getString("log_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Object o) {

                }
            });
            return user;
        } catch (Exception e) {
            Log.e("TAG", "增加用户失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static UserBean identify(final UserBean user, HttpCallBack callBack) {
        // 返回用户top数，默认为1
        String userTopNum = "1";
        // 单用户人脸匹配得分top数，默认为1
        String faceTopNum = "1";
        try {
            byte[] imgData1 = FileUtil.readFileByBytes(user.getImages());
            String imgStr1 = Base64Util.encode(imgData1);

            String params = "group_id=" + user.getGroup_id() + "&user_top_num=" + userTopNum + "&face_top_num" + faceTopNum
                    + "&images=" + URLEncoder.encode(imgStr1, "UTF-8");
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            HttpUtil.post(URL_IDENTIFY, params, callBack);
            return user;
        } catch (Exception e) {
            Log.e("TAG", "识别失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
