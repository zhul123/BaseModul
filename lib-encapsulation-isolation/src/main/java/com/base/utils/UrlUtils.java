package com.base.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.Postcard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :  zl
 * @desc :
 */

public class UrlUtils {

    /**
     * 返回数组?分隔,[0]?前面的,[1]?后面的参数
     *
     * @return
     */
    public static String[] splitUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return url.split("\\?");
    }

    /**
     * url转换，根据传入的url转换成app内部用的path
     *
     * @param urlMap
     * @param url
     * @return
     */
    public static Uri urlTransformationUri(Map<String, String> urlMap, String url) {
        String path = url;
        if (!TextUtils.isEmpty(url) && null != splitUrl(url) && splitUrl(url).length > 0) {
            String[] urlSplit = splitUrl(url);
            String tmpPath = urlMap.get(urlSplit[0]);
            if (TextUtils.isEmpty(tmpPath)) {
                path = url;
            } else {
                path = tmpPath + "?" + (urlSplit.length > 1 ? urlSplit[1] : "");
            }

        }
        return Uri.parse(path);
    }


    /**
     * 根据Uri获取参数map
     *
     * @param uri
     * @return
     */
    public static Map<String, String> getParameterMapByUri(Uri uri) {
        Map<String, String> map = new HashMap<>();
        if (null == uri || TextUtils.isEmpty(uri.toString())) {
            return map;
        }
        for (String key : uri.getQueryParameterNames()) {
            map.put(key, uri.getQueryParameter(key));
        }
        return map;
    }

    /**
     * 将uri中的参数解析出来增加到Postcard中
     *
     * @param postcard
     * @param uri
     * @return
     */
    public static Postcard addUriParameter(Postcard postcard, Uri uri) {
        Map<String, String> map = getParameterMapByUri(uri);
        for (String key : map.keySet()) {
            postcard.withString(key, map.get(key));
        }
        return postcard;
    }

    /**
     * 将urlString参数拼接到path后面
     *
     * @param path
     * @param urlString
     * @return
     */
    public static String urlStringEncode(String path, String urlString) {
        String encodeUrl = "";
        try {
            if (path.contains("?")) {
                encodeUrl = path + "&urlString=" + URLEncoder.encode(urlString, "UTF-8");
            } else {
                encodeUrl = path + "?urlString=" + URLEncoder.encode(urlString, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodeUrl = "";
        }
        return encodeUrl;
    }

    /**
     * 更新uri 中的parameter,如果没有就添加,会对value进行encode utf-8
     *
     * @param uri
     * @param key
     * @param value
     * @return
     */
    public static Uri updateUriParameter(Uri uri, String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key,value);
        return updateUriParameters(uri,map);
    }

    public static Uri updateUriParameters(Uri uri, Map<String, String> parameterMap){
        if(null == uri
                || null == parameterMap){
            return null;
        }
        Map<String, String> uriParameter = getParameterMapByUri(uri);
        uriParameter.putAll(parameterMap);
        Uri.Builder builder = uri.buildUpon();
        builder.clearQuery();
        for (String parameter : uriParameter.keySet()) {
            builder.appendQueryParameter(parameter, uriParameter.get(parameter));
        }
        return builder.build();
    }


    /**
     * 工厂方法，生成ARouterUri；
     *
     * @param uri
     * @param isLogin         1需要登录，2不需要登录
     * @param alreadyLogin    1已经登录，2未登录
     * @param identity        1需要绑定信息，2不需要绑定信息
     * @param alreadyIdentity 1已经绑定，2未绑定
     * @return
     */
    public static final Uri factoryARouterUri(Uri uri, int isLogin, int alreadyLogin, int identity, int alreadyIdentity) {
        Map<String, String> map = new HashMap<>();
//        map.put(LOGIN, String.valueOf(alreadyLogin));
//        map.put(NEED_LOGIN, String.valueOf(isLogin));
//        map.put(IDENTITY, String.valueOf(alreadyIdentity));
//        map.put(NEED_IDENTITY, String.valueOf(identity));
        return updateUriParameters(uri,map);

    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick(long duration) {
        long time = System.currentTimeMillis();
        if (Math.abs(time - lastClickTime) < duration) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
