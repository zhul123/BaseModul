package com.capinfo.statistics;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.capinfo.statistics.Interceptor.EVENT_WRAPPER_KEY;
import static com.capinfo.statistics.Interceptor.EXPAND_WRAPPER_KEY;
import static com.capinfo.statistics.Interceptor.LABEL_WRAPPER_KEY;

/**
 * @author :  zhulei
 * @desc : 各种上报方式的基类
 */
public abstract class AbsAppender<K, V> implements Appender<K, V> {

    private List<Interceptor> mInterceptors = new ArrayList<>();

    public void addInterceptors(List<Interceptor> interceptors) {
        mInterceptors.addAll(interceptors);
    }

    @Override
    public void appender(Context context, String event, String label, Map<K, V> map) {
        Object expand = null;
        for (Interceptor interceptor : mInterceptors) {
            Map<String, String> eventWrapper = new HashMap<>();
            eventWrapper.put(EVENT_WRAPPER_KEY, event);
            Map<String, String> labelWrapper = new HashMap<>();
            labelWrapper.put(LABEL_WRAPPER_KEY, label);
            Map<String, Object> expandWrapper = new HashMap<>();
            interceptor.interceptor(context, eventWrapper, labelWrapper, map, expandWrapper);
            event = eventWrapper.get(EVENT_WRAPPER_KEY);
            label = labelWrapper.get(LABEL_WRAPPER_KEY);
            expand = expandWrapper.get(EXPAND_WRAPPER_KEY);
        }
        doAppender(context, event, label, map, expand);
    }

    @Override
    public void resume(Context context, Map<K, V> map) {
        Object expand = null;
        for (Interceptor interceptor : mInterceptors) {
            Map<String, Object> expandWrapper = new HashMap<>();
            interceptor.interceptor(context, null, null, null, expandWrapper);
            expand = expandWrapper.get(EXPAND_WRAPPER_KEY);
        }
        onResume(context, expand,map);
    }

    @Override
    public void pause(Context context, Map<K, V> map) {
        Object expand = null;
        for (Interceptor interceptor : mInterceptors) {
            Map<String, Object> expandWrapper = new HashMap<>();
            interceptor.interceptor(context, null, null, null, expandWrapper);
            expand = expandWrapper.get(EXPAND_WRAPPER_KEY);
        }
        onPause(context, expand,map);
    }

    public abstract void onResume(Context context, Object expand, Map<K, V> map);

    public abstract void onPause(Context context, Object expand, Map<K, V> map);

    public abstract void doAppender(Context context, String event, String label, Map<K, V> map, Object expand);

    public void flush() {
    }

    public void release() {
    }
}
