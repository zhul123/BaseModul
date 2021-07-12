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
    interface Path {
        String GROUP = "/chat";
        String PROVIDER_PATH = "/provider" + GROUP;
        String PATH_MAIN = GROUP + "/mainPage";
        String PATH_WEBVIEW = GROUP + "/webView";
        String PATH_PLATFORM_SET = GROUP + "/platformSet";
        String PATH_PLATFORM_ADD = GROUP + "/platformAdd";
        //公共webview加载
        String COMMON_WEBVIEW_PATH = GROUP + "/commonWebViewPath";
    }

    interface ConstantDef{
        String WEBVIEW_APP = "webview_app";
        String WEBVIEW_url = "webview_url";
    }



    void gotoActivityByARouterUri(Uri uri);

    void gotoActivityByARouterUri(Uri uri, Activity activity, int requestCode);

    void gotoActivityByARouterUri(Uri uri, Context context, NavigationCallback callback);

    void gotoMainActivity(int selectedIndex);

    void gotoMainActivity(Context context, int selectedIndex, NavigationCallback callback);

    void gotoCommonWebViewActivity(String urlString);

    void gotoCommonWebViewActivity(String urlString, String extendParam);

    void gotoLoginActivity();

    /**
     *前往微信小程序
     * @param username 填小程序原始id
     * @param path 拉起小程序页面的可带参路径，不填默认拉起小程序首页
     */
    void gotoWxMiniProgress(String username, String path);

}
