package com.component.providers.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.android.arouter.exception.HandlerException;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.component.R;
import com.base.utils.ARouterUtils;
import com.base.utils.AppToast;
import com.base.utils.UrlUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author :  zl
 * @desc :
 */
@Route(path = AppProvider.PROVIDER_PATH, name = "App模块")
public class AppProviderImpl implements AppProvider {

    private Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
        ARouterUtils.init(context);
    }

    @Override
    public void gotoActivityByARouterUri(Uri uri) {
        Uri tmpUri = getARouterUriByUri(uri);
        if (null == tmpUri || TextUtils.isEmpty(tmpUri.toString())) {
            return;
        }
        navigation(tmpUri, null, null);
    }

    @Override
    public void gotoActivityByARouterUri(Uri uri, Activity activity, int requestCode) {
        Uri tmpUri = getARouterUriByUri(uri);
        if (null == tmpUri || TextUtils.isEmpty(tmpUri.toString())) {
            return;
        }
        navigation(tmpUri, activity, requestCode, null);

    }

    @Override
    public void gotoActivityByARouterUri(Uri uri, Context context, NavigationCallback callback) {
        Uri tmpUri = getARouterUriByUri(uri);
        if (null == tmpUri || TextUtils.isEmpty(tmpUri.toString())) {
            return;
        }
        navigation(tmpUri, context, callback);
    }

    private void navigation(Uri uri, Context context, NavigationCallback callback) {
        try {
            if (null != context && null != callback) {
                ARouter.getInstance().build(uri).navigation(context, callback);
            } else if (null != context) {
                ARouter.getInstance().build(uri).navigation(context);
            } else {
                ARouter.getInstance().build(uri).navigation();
            }
            //抛出异常，防止uri不对的情况下app崩溃
        } catch (HandlerException e) {
//            Toast.makeText(mContext, R.string.err_uri, Toast.LENGTH_SHORT).show();
            AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(R.string.err_uri);
        }
    }

    private void navigation(Uri uri, Activity context, int requestCode, NavigationCallback callback) {
        try {
            if (null != context && null != callback) {
                ARouter.getInstance().build(uri).navigation(context, requestCode, callback);
            } else if (null != context) {
                ARouter.getInstance().build(uri).navigation(context, requestCode);
            } else {
                ARouter.getInstance().build(uri).navigation();
            }
//            //抛出异常，防止uri不对的情况下app崩溃
        } catch (HandlerException e) {
//            Toast.makeText(mContext, R.string.err_uri, Toast.LENGTH_SHORT).show();
            AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(R.string.err_uri);
        }
    }

    private Uri getARouterUriByUri(Uri uri) {
        String path = uri.getPath();
        //判断地址是否为空，是否包含协议头
        if (TextUtils.isEmpty(path) || path.indexOf(ARouterUtils.AROUTERRULE) == -1) {
            AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(R.string.err_path_empty);
        }
        try {
            path = path.replaceFirst(ARouterUtils.AROUTERRULE, "");
        }catch (Exception e){
            AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(R.string.err_path_empty);
        }
        uri = uri.buildUpon().path(path).build();
        return uri;
    }

    @Override
    public void gotoMainActivity(int selectedIndex) {
        gotoActivityByARouterUri(Uri.parse(AppProvider.PATH_MAIN + "?selectedIndex=" + selectedIndex));
    }

    @Override
    public void gotoMainActivity(Context context, int selectedIndex, NavigationCallback callback) {
        gotoActivityByARouterUri(Uri.parse(AppProvider.PATH_MAIN + "?selectedIndex=" + selectedIndex), context, callback);
    }

    @Override
    public void gotoCommonWebViewActivity(String urlString) {
        gotoCommonWebViewActivity(urlString,"");
    }
    @Override
    public void gotoCommonWebViewActivity(String urlString, String extendParam) {
        String tmpUrlString = "";
        try {
            tmpUrlString = AppProvider.COMMON_WEBVIEW_PATH + "?extend_param=" + URLEncoder.encode(extendParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString = UrlUtils.urlStringEncode(tmpUrlString + "&show_webview_title=false", urlString);
        UrlUtils.addUriParameter(ARouter.getInstance().build(Uri.parse(urlString)), Uri.parse(urlString)).navigation();
    }

    @Override
    public void gotoLoginActivity() {

    }

    @Override
    public void gotoWxMiniProgress(String username, String path) {
        /*Mlog.d("---定制公交 微信小程序----");
        String appId = AppChannelUtil.getMetaDataStr("MINICHAT_ID");
        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), appId);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_f17dafa3c593"; // 填小程序原始id
//        req.path = "拉起小程序页面的可带参路径";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);*/
    }

}
