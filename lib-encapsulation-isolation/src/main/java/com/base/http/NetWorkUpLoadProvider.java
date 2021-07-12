package com.base.http;


import com.base.common.BaseProvider;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 2019-10-17 14:53
 * @desc:
 */
public interface NetWorkUpLoadProvider extends BaseProvider {
    String GROUP = "/uploadfile";
    String PROVIDER_PATH = "/netWorkUpLoadProvider" + GROUP;
    boolean isNetworkAvailable();

}
