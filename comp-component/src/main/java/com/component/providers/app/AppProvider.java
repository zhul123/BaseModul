package com.component.providers.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.component.providers.base.BaseProvider;

/**
 * @author :  zl
 * @desc : App模块对外提供的方法
 */

public interface AppProvider extends BaseProvider {
    String GROUP = "/app";
    String PROVIDER_PATH = "/provider" + GROUP;
    String PATH_MAIN = GROUP + "/mainPage";
    //公共webview加载
    String COMMON_WEBVIEW_PATH = GROUP + "/commonWebViewPath";

    public void gotoActivityByARouterUri(Uri uri);

    public void gotoActivityByARouterUri(Uri uri, Activity activity, int requestCode);

    public void gotoActivityByARouterUri(Uri uri, Context context, NavigationCallback callback);

    public void gotoMainActivity(int selectedIndex);

    public void gotoMainActivity(Context context, int selectedIndex, NavigationCallback callback);

    public void gotoCommonWebViewActivity(String urlString);

    public void gotoCommonWebViewActivity(String urlString, String extendParam);

    public void gotoLoginActivity();

    /**
     *前往微信小程序
     * @param username 填小程序原始id
     * @param path 拉起小程序页面的可带参路径，不填默认拉起小程序首页
     */
    public void gotoWxMiniProgress(String username, String path);

}
