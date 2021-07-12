package com.lib.block.entity.base;

import java.io.Serializable;

public class EventEntity implements Serializable {
    private String jumpType;//跳转类型
    private String urlString;//跳转url参数
    private String arouterUrl;//跳转路由地址

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getArouterUrl() {
        return arouterUrl;
    }

    public void setArouterUrl(String arouterUrl) {
        this.arouterUrl = arouterUrl;
    }
}
