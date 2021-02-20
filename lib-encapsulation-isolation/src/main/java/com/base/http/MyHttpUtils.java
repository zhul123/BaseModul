package com.base.http;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.http.okhttp.OkHttpUtils;
import com.base.http.okhttp.builder.GetBuilder;
import com.base.http.okhttp.builder.PostStringBuilder;
import com.base.http.okhttp.callback.Callback;
import com.base.http.okhttp.callback.StringCallback;
import com.base.http.okhttp.log.LoggerInterceptor;
import com.capinfo.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MyHttpUtils {
    public enum MediaTypeMap{
        JSON;
    }
    private static final String MEDIATYPE = "application/json";
    private static final String ERRMSG = "请求失败请重试。";
    private static final String TAG = "okhttp";
    private static final String SUCCESSKEY = "success";
    private static final String RESULTKEY = "result";
    private static final String MESSAGEKEY = "message";
    public interface HttpCallBack<T>{
        public void onSuccess(T result);
        public void onFail(String errMsg);
    }

    public static void init(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("com.http"))
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void post(String url, Map params, HttpCallBack callBack){
        post(url,params, MyHttpUtils.MediaTypeMap.JSON,callBack);
    }

    public static void post(String url, Map params,MediaTypeMap mediaType, HttpCallBack callBack){
        if(TextUtils.isEmpty(url)){
            return;
        }
        PostStringBuilder postStringBuilder = OkHttpUtils.postString().url(url);

        if(params != null) {
            postStringBuilder.content(JSON.toJSONString(params));
            if(BuildConfig.DEBUG){
                Log.e(TAG,"params:"+JSON.toJSONString(params));
            }
        }else{
            postStringBuilder.content("{}");
        }
        switch (mediaType){
            case JSON:
                postStringBuilder.mediaType(MediaType.parse(MEDIATYPE));
                break;
        }
        if(callBack != null) {
            postStringBuilder.build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    callBack.onFail(ERRMSG);
                    if(BuildConfig.DEBUG) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    if(TextUtils.isEmpty(response)){
                        callBack.onFail(ERRMSG);
                    }else{
                        JSONObject jsonObject = JSON.parseObject(response);
                        if(jsonObject == null){
                            callBack.onFail(ERRMSG);
                        }else{
                            if (jsonObject.getBoolean(SUCCESSKEY)) {
                                callBack.onSuccess(jsonObject.getJSONObject(RESULTKEY));
                            } else {
                                String message = jsonObject.getString(MESSAGEKEY);
                                callBack.onFail(TextUtils.isEmpty(message) ? ERRMSG : message);
                            }
                        }
                    }
                }
            });
        }else{
            postStringBuilder.build().execute(null);
        }
    }

    public static void get(String url, HashMap params,HttpCallBack callBack){
        if(TextUtils.isEmpty(url))
            return;
        GetBuilder getBuilder = OkHttpUtils.get().url(url);
        if(params != null){
            getBuilder.params(params);
        }
        if(callBack != null) {
            getBuilder.build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    callBack.onFail(ERRMSG);
                }

                @Override
                public void onResponse(String response, int id) {
                    if(TextUtils.isEmpty(response)){
                        callBack.onFail(ERRMSG);
                    }else{
                        JSONObject jsonObject = JSON.parseObject(response);
                        if(jsonObject == null){
                            callBack.onFail(ERRMSG);
                        }else{
                            if (jsonObject.getBoolean(SUCCESSKEY)) {
                                callBack.onSuccess(jsonObject.getString(RESULTKEY));
                            } else {
                                String message = jsonObject.getString(MESSAGEKEY);
                                callBack.onFail(TextUtils.isEmpty(message) ? ERRMSG : message);
                            }
                        }
                    }
                }
            });
        }else {
            getBuilder.build().execute(null);
        }
    }
}
