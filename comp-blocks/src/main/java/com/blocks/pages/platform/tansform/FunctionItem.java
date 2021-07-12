package com.blocks.pages.platform.tansform;
import java.io.Serializable;

public class FunctionItem implements Serializable {

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







}
