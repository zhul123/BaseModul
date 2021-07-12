package com.capinfo.statistics;

import android.content.Context;

import java.util.Map;

/**
 * @author : zhulei
 * @desc : 各种上报方式的接口
 */
public interface Appender<K,V> {

    void appender(Context context, String event, String label, Map<K,V> map);
    void resume(Context context, Map<K, V> map);
    void pause(Context context, Map<K, V> map);
    void flush();
    void release();

}
