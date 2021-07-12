package com.base.utils.savedata.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.base.app.BaseApplication;


/**
 * @ClassName: AppSharedPreferencesHelper
 * @Description: (公用类 ， 用于缓存一些key — — value类型的数据)
 */

public class AppSharedPreferencesHelper {
    private static SharedPreferences mSharedPreferences;
    private static Editor mEditor;
    public static final String APP_PREFS = "capinfo_prefs";
    public static final String TOKEN = "token";
    public static final String RESULT = "result";
    public static final String USERINFO = "userInfo";
    /**
     * Get SharedPreferences
     */
    private static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    public static SharedPreferences getSharedPreferences() {

        if (mSharedPreferences == null) {
            BaseApplication baseApplication = BaseApplication.getInstance();
            if (null != baseApplication) {
                mSharedPreferences = baseApplication.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
            }
        }
        return mSharedPreferences;
    }

    public static Editor getEditor() {

        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        return mEditor;
    }

    public static void clear() {
        mEditor = getEditor();
        mEditor.clear();
        mEditor.commit();
    }

    public static String getToken() {
        return getSharedPreferences().getString(TOKEN, "");
    }
    public static String getResult() {
        return mSharedPreferences.getString(RESULT, "");
    }

    public static void setToken(String token) {
        System.out.println("=======setToken:"+token);
        if(TextUtils.isEmpty(token)){
            getEditor().remove(TOKEN).commit();
        }else {
            getEditor().putString(TOKEN, token).commit();
        }
    }

    public static void setUserInfo(JSONObject userInfo){
        if(userInfo != null){
            getEditor().putString(USERINFO,userInfo.toString()).commit();
        }else{
            getEditor().remove(USERINFO).commit();
        }
    }

    public static String getUserInfo(){
        return getSharedPreferences().getString(USERINFO,"");
    }

    public static void setResult(String result) {
        System.out.println("=======setResult:"+result);
        if(TextUtils.isEmpty(result)){
            getEditor().remove(RESULT).commit();
        }else {
            getEditor().putString(RESULT, result).commit();
        }
    }
}
