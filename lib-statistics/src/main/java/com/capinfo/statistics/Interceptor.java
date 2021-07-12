package com.capinfo.statistics;

import android.content.Context;

import java.util.Map;

/**
 * @author :  zhulei
 * @desc : 拦截器
 */
public interface Interceptor<K, V> {

    String EVENT_WRAPPER_KEY = "event";
    String LABEL_WRAPPER_KEY = "label";
    String EXPAND_WRAPPER_KEY = "expand";

    /**
     * 拦截器，每次上报都会调用，参数都用了Map形式为了可以动态修改参数
     *
     * @param context
     * @param eventWrapper key为固定的event
     * @param labelWrapper key为固定的label
     * @param map
     * @param expandWrapper       扩展字段
     */
    void interceptor(Context context, Map<String, String> eventWrapper, Map<String, String> labelWrapper, Map<K, V> map, Map<String, Object> expandWrapper);

}
