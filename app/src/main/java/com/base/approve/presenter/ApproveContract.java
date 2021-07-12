package com.base.approve.presenter;

import com.component.base.IPresenter;
import com.component.base.IView;

public class ApproveContract {

    public interface ApproveView extends IView{
        public void showView(String result);
    }

    public interface ApprovePresenter extends IPresenter {
        //获取跳转应用参数
        public void getFunctionValue(String corpId , String appId);
        //获取智能审批token
        public void getApproveToken(String code);
        //获取智能审批列表
        public void getApproveTree(String token);
        //获取智能审批事项对应列表
        public void getApproveItemList(String cas01Code,String cas01Guid);
    }

}
