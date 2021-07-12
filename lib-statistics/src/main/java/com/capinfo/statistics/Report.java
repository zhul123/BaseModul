package com.capinfo.statistics;

import android.content.Context;

import java.util.Map;

/**
 * @author :  zhulei
 * @desc : 统计上报类
 */
public class Report {

    private static Statistics mDelegate;


    public static void Init(Statistics statistics){
        mDelegate = statistics;
    }

    /**
     * 是否初始化
     * @return true初始化，false没有初始化
     */
    public static boolean isInit(){
        return (null != mDelegate);
    }

    public static void reportEvent(String event, String label, Map<String, String> params) {
        if(null != mDelegate){
            mDelegate.reportEvent(event,label,params);
        }
    }

    public static void onResume(Context context, Map<String, String> params){
        if(null != mDelegate){
            mDelegate.reportResume(context,params);
        }
    }

    public static void onPause(Context context, Map<String, String> params){
        if(null != mDelegate){
            mDelegate.reportPause(context,params);
        }
    }
}
