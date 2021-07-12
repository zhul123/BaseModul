package com.block.transform.platform;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class PlatformBean implements Serializable {

    public String appId;
    public String appName;
    public String appType;
    public String appTypeName;
    public String isOld;
    public String isWx;
    public String logoUrl;
    public String sort;
    public String type;

    public String appUrl;
    public String corpId;
    public String url;

    public boolean isSelect = true;   //默认情况，在互联网取到的选中状态

    public int functionType = 1;  // -1、更多按钮   1、原生

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getIsOld() {
        return isOld;
    }

    public void setIsOld(String isOld) {
        this.isOld = isOld;
    }

    public String getIsWx() {
        return isWx;
    }

    public void setIsWx(String isWx) {
        this.isWx = isWx;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppTypeName() {
        return appTypeName;
    }

    public void setAppTypeName(String appTypeName) {
        this.appTypeName = appTypeName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }
}
