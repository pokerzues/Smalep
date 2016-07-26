package com.tyutiot.smalepv2;

import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This is a class that is used to receiver and handle masseges from BaiduPush.
 */
public class MyPushMessageReceiver extends PushMessageReceiver {

    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

    /**
     * 调用 PushManager.startWork 后,sdk 将对 push server 发起绑定请求,这个过程是异步
     * 的。绑定请求的结果通过 onBind 返回。
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
    }

    /**
     * 接收透传消息的函数。
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=" + message + " customContentString="
                + customContentString;
        Log.d(TAG, messageString);
// 自定义内容获取方式,mykey 和 myvalue 对应透传消息推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收通知点击的函数。
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知点击 title=" + title + " description="
                + description + " customContent=" + customContentString;
        Log.d(TAG, notifyString);
// 自定义内容获取方式,mykey 和 myvalue 对应通知推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容,为空或者 json 字符串
     */
    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知到达 title=" + title + " description="
                + description + " customContent=" + customContentString;
        Log.d(TAG, notifyString);
// 自定义内容获取方式,mykey 和 myvalue 对应通知推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * setTags() 的回调函数。
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode + " sucessTags="
                + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
    }

    /**
     * delTags() 的回调函数。
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode + " sucessTags="
                + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
    }

    /**
     * listTags() 的回调函数。
     */
    @Override
    public void onListTags(Context context, int errorCode,
                           List<String> tags, String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
    }

    /**
     * PushManager.stopWork() 的回调函数。
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
    }

}
