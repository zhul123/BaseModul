package com.lib.block.style;

import java.io.Serializable;

public class Params implements Serializable {



    //  --------公共参数开始----------------
    public static final String STYLE = "style";//为了复用、扩展cell中的style
    public static final String DATAS = "datas";//为了复用、扩展cell中的style
    public static final String MODEL = "model";//为了复用、扩展cell中的style
    public static final String TYPE = "type";//
    public static final String ITEMS = "items";//
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String LOCALRES = "drawable://";

    //checkbox
    public static final String CHECKSTATE = "checkState";// checkbox 选择状态

    public static final String MUSTINPUT = "mustInput";// 是否必须输入
    public static final String MUSTCHECK = "mustCheck";// 是否必须选择
    public static final String CHOOSEDATAS = "chooseDatas";//下拉框选择数据源
    public static final String TEXT = "text";//文字描述 类型String
    public static final String WARNTEXT = "warnText";//警告文字描述 类型String
    public static final String PARAMSKEY = "paramsKey";//默认入参参数名，对应控件中edittex
    public static final String OTHERPARAMSKEY = "otherParamsKey";//组件中设置的其他参数
    public static final String FIXARAMSKEY = "fixParamsKey";//设置固定参数
    public static final String ENCRYPTIONKEYS = "encryptionKeys";//需要加密参数key值，多个参数英文分号隔开;
    public static final String URL = "url";// 请求地址
    public static final String IMGURL = "imgUrl";// 图片地址 类型String
    public static final String CHECKKEY = "checkKey";//验证码时间戳
    public static final String URLSTRING = "urlString";// webview 跳转地址
    public static final String AROUTERURL = "arouterUrl";// webview 跳转地址
    public static final String HAVETITLEBAR = "haveTitleBar";// webview 跳转地址
//  --------公共参数结束----------------
}
