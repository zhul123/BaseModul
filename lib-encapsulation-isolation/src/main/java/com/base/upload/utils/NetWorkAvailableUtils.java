package com.base.upload.utils;


import com.alibaba.android.arouter.launcher.ARouter;
import com.base.common.BaseRouterManager;
import com.base.http.NetWorkUpLoadProvider;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 2019-10-17 14:09
 * @desc:
 */
public class NetWorkAvailableUtils extends BaseRouterManager {


    private static NetWorkUpLoadProvider mNetWorkUpLoadProvider;

    private final static NetWorkUpLoadProvider getProvider() {
        if (null == mNetWorkUpLoadProvider) {
            mNetWorkUpLoadProvider = (NetWorkUpLoadProvider) ARouter.getInstance().build(NetWorkUpLoadProvider.PROVIDER_PATH).navigation();
        }
        return mNetWorkUpLoadProvider;
    }

    public static boolean isNetworkAvailable() {
        if(getProvider()==null){
            return false;
        }
        return getProvider().isNetworkAvailable();

    }

}
