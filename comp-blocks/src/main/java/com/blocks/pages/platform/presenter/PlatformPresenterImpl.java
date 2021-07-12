package com.blocks.pages.platform.presenter;

import com.base.http.MyHttpUtils;

import java.util.HashMap;

public class PlatformPresenterImpl implements PlatformContract.PlatformPresenter{
    private String platfromListUrl = "http://imtest.bgosp.com:80/admin/api/applications/";
    private String corpUrl = "http://imtest.bgosp.com:80/admin/user-setting/me/org/corp";

    PlatformContract.PlatformView mPlatformView;

    private String access_token = "Bearer" + " " + "9dcbe1588268eecbd7e389808221da8c";

    public PlatformPresenterImpl(PlatformContract.PlatformView view){
        mPlatformView = view;
    }

    @Override
    public void getCorp(String access_token) {
        mPlatformView.showLoadingView();
        HashMap header = new HashMap(1);
        header.put("Authorization",this.access_token);
        MyHttpUtils.get(corpUrl, header, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======getCorp:"+result);
                getPlatformList("319525768fc243c690b3448d4c3f2e31");
            }

            @Override
            public void onFail(String errMsg) {
                mPlatformView.hideLoadingView();
                System.out.println("======getCorp:err"+errMsg);
            }
        },this);
    }

    @Override
    public void getPlatformList(String corpId) {
        platfromListUrl = platfromListUrl + corpId;
        HashMap header = new HashMap(1);
        header.put("Authorization",access_token);
        MyHttpUtils.get(platfromListUrl, header, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======getPlatfromList:"+result);
                mPlatformView.showView(result);
                mPlatformView.hideLoadingView();
            }

            @Override
            public void onFail(String errMsg) {
                mPlatformView.hideLoadingView();
                System.out.println("======getPlatfromList:err"+errMsg);
            }
        },this);
    }

    @Override
    public void onDestroy() {
        MyHttpUtils.destroy(this);
    }

    @Override
    public void unRegisterDispose() {

    }
}
