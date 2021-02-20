package com.component.providers.common;


import com.component.providers.base.BaseProvider;

public interface CommonProvider extends BaseProvider {
    interface Path {
        String GROUP = "/common";
        //入口
        String PROVIDER_PAY_PATH = "/commonprovider" + GROUP;
    }

    interface CommonConstantDef {
        String WEBVIEW_FUNCTIONITEM = "app";
        String LOCALDATAURL = "localDataUrl";
        String URLSTRING = "urlString";
        String TITLE = "title";
        String HAVETITLEBAR = "haveTitleBar";
    }
}
