package com.base.approve.presenter;

import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.base.http.MyHttpUtils;
import com.base.utils.savedata.DataUtil;

import java.util.HashMap;

public class ApprovePresenterImpl implements ApproveContract.ApprovePresenter{
    private String fuctionUrl = "http://219.232.207.218:80/admin/api/applications/";
    private String tokenUrl = "http://219.232.207.218/jeecg-boot/sys/user/getTokenByCode";
    private String treeUrl = "http://219.232.207.218/jeecg-boot/cas/api/de/tree?cas01App=1";
    private String caseListUrl = "http://219.232.207.218/jeecg-boot/cas/api/getCaseList?pageNo=1&pageSize=1000000&cas01App=1";

    ApproveContract.ApproveView mApproveView;

    private static final String  TOKEN = "approve_token";

    public ApprovePresenterImpl(ApproveContract.ApproveView view){
        mApproveView = view;
    }

    @Override
    public void getFunctionValue(String corpId, String appId) {
        //corpid 54863eb3e86a42ca8c0b99ee296e7d6d
        //appId sx0941660f1924b4d80
        fuctionUrl = fuctionUrl +"54863eb3e86a42ca8c0b99ee296e7d6d/sx0941660f1924b4d80";
        String access_token = "Bearer" + " " + "a536e5c26607fdf6cfd9addfa79c4e11";
        HashMap header = new HashMap();
        header.put("Authorization",access_token);
        mApproveView.showLoadingView();
        MyHttpUtils.get(fuctionUrl, header, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======function:"+result);
                try{
                    JSONObject jsonObject = (JSONObject) JSON.parse(result);
                    String url = jsonObject.getJSONObject("data").getString("url");
                    String[] urlStr = url.split("&");
                    String code = "";
                    for(String str : urlStr){
                        if(str.indexOf("code=") != -1){
                            code = str.replace("code=","");
                        }
                    }
                    System.out.println("======function:code="+code);
                    getApproveToken(code);
                }catch (Exception e){

                }
            }

            @Override
            public void onFail(String errMsg) {
                mApproveView.hideLoadingView();
                System.out.println("======function:err"+errMsg);
            }
        },this);
    }

    @Override
    public void getApproveToken(String code) {
        tokenUrl = tokenUrl + String.format("?type=1&code=%s&appType=cap",code);
        MyHttpUtils.get(tokenUrl, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======token:"+result);
                DataUtil.getInstance().put(TOKEN,result);
                getApproveTree(result);
            }

            @Override
            public void onFail(String errMsg) {

                mApproveView.hideLoadingView();
                System.out.println("======getApproveToken:err"+errMsg);
            }
        },this);
    }

    @Override
    public void getApproveTree(String token) {
        HashMap header = new HashMap(1);
        header.put("TOKEN",token);
        MyHttpUtils.get(treeUrl, header, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======tree:"+result);
                mApproveView.showView(result);
                mApproveView.hideLoadingView();
                getApproveItemList("","");
            }

            @Override
            public void onFail(String errMsg) {
                mApproveView.hideLoadingView();
                System.out.println("======getApproveTree:err"+errMsg);
            }
        },this);
    }

    @Override
    public void getApproveItemList(String cas01Code, String cas01Guid) {
        cas01Code = "af639139-91b4-4859-8bc6-da4b0fc10aa5";
        cas01Guid = "30bc3ce8d8f674b4c5344d16c1f8d1e8";
        caseListUrl = caseListUrl +"&cas01Code="+cas01Code+"&cas01Guid="+cas01Guid;
        HashMap header = new HashMap(1);
        header.put("TOKEN",DataUtil.getInstance().getString(TOKEN));
        MyHttpUtils.get(caseListUrl, header, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======getApproveItemList:"+result);
                mApproveView.showView(result);
                mApproveView.hideLoadingView();
            }

            @Override
            public void onFail(String errMsg) {
                mApproveView.hideLoadingView();
                System.out.println("======getApproveTree:err"+errMsg);
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
