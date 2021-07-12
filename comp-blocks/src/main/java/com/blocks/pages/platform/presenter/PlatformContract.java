package com.blocks.pages.platform.presenter;
import com.component.base.IPresenter;
import com.component.base.IView;

public class PlatformContract {

    public interface PlatformView extends IView{
        public void showView(String result);
    }

    public interface PlatformPresenter extends IPresenter {
        //获取设置的corp信息
        public void getCorp(String access_token);
        //获取工作台列表
        public void getPlatformList(String corpId);
    }

}
