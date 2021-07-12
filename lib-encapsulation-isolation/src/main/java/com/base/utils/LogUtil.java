package com.base.utils;


import android.util.Log;

public class LogUtil {
    public static void e(String tag, String msg){
        Log.e(tag,msg);
    }

    public static void e(String tag, String msg, Object... obj){
        Log.e(tag,msg);
    }

    public static void d(String tag, String msg){
        Log.d(tag,msg);
    }

    public static void d(String tag, String msg, Object... obj){
        Log.d(tag,msg);
    }

    public static void i(String tag, String msg){
        Log.i(tag,msg);
    }
    public static void i(String tag, String msg, Object... obj){
        Log.i(tag,msg);
    }
}
