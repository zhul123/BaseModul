package com.blocks.views.utils;

import android.text.TextUtils;
import android.util.Log;

import com.blocks.views.AesEncryptUtil;
import com.blocks.views.floorviews.EditFloorView;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamsUtils {
    //  --------公共参数开始----------------
    public static final String STYLE = "style";//为了复用、扩展cell中的style
    public static final String DATAS = "datas";//为了复用、扩展cell中的style

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
    public static final String HAVETITLEBAR = "haveTitleBar";// webview 跳转地址
//  --------公共参数结束----------------

    //  -----------style属性开始------------
    public static final String RADIUS = "radius";//圆角 类型int 单位dp
    public static final String RADIUSCOLOR = "radiusBgColor";//圆角控件背景颜色
    public static final String IMGHEIGHT = "imgHeight";// 图片高度 类型int 单位dp
    public static final String IMGWIDTH = "imgWidth";//图片宽度 类型int 单位dp
    public static final String IMGPADDING = "imgPadding";//图片padding 类型int 单位dp
    public static final String IMGMARGIN = "imgMargin";//图片padding 类型int 单位dp
    public static final String TEXTSIZE = "textSize";//字体大小 类型int 单位sp
    public static final String TEXTCOLOR = "textColor";//字体颜色 类型string 例如#FFFFF
    public static final String TEXTSTYLE = "textStyle";//字体样式 类型string （italic 斜体，bold 加粗，nomal 默认）
    public static final String GRAVITY = "gravity";//居中方式 （center,left,right）
    public static final String RATIO = "aspectRatio";// 宽/高的值  类型float 默认是1
    //  -----------style属性结束------------

    private volatile static ParamsUtils instance = null;//volatile 确保本条指令不会因编译器的优化而省略

    public static ParamsUtils getInstance() {
        if (instance == null) {
            synchronized (ParamsUtils.class) {
                if (instance == null) {
                    instance = new ParamsUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 请求入参校验合并
     *
     * @param cardList
     * @return
     */
    public HashMap complexAndCheckParams(List<Card> cardList) throws Exception {
        if (cardList == null)
            return null;
        List<BaseCell> cells = new ArrayList<>();
        for (Card card : cardList) {
            cells.addAll(card.getCells());
        }

        return complexAndCheckParamsByList(cells);
    }

    /**
     * 请求入参
     *
     * @param baseCell
     * @return
     */
    public HashMap complexParams(BaseCell baseCell) {
        HashMap<String, String> params = new HashMap<>(4);
        if (baseCell.parent == null)
            return params;
        try {
            if (baseCell != null) {
                List<BaseCell> allCell = baseCell.parent.getCells();
                params.putAll(complexAndCheckParamsByList(allCell));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public HashMap complexAndCheckParamsByList(List<BaseCell> allCell) throws Exception {
        HashMap<String, String> params = new HashMap<>(4);
        if (allCell == null)
            return params;
        for (BaseCell cell : allCell) {
            checkParams(cell);
            //设置默认参数
            setDefaultParams(cell, params);
            //设置其他参数
            setOtherParams(cell, params);
            //设置固定参数
            setFixParams(cell, params);
        }
        return params;
    }

    private void checkParams(BaseCell cell) throws CheckMustException {
        if (getBoolean(cell, MUSTINPUT)) {
            String param = getOptString(cell, PARAMSKEY);
            Object paramObject = cell.getAllBizParams().get(param);
            if (paramObject == null  || TextUtils.isEmpty(paramObject.toString())) {
                throw new CheckMustException(getOptString(cell, WARNTEXT));
            }
        }
        //是否必须选择（常用于条款阅读）
        if(getBoolean(cell,MUSTCHECK)){
            if(!getBoolean(cell,CHECKSTATE)){
                throw new CheckMustException(getOptString(cell, WARNTEXT));
            }
        }
    }

    /**
     * 设置默认参数 注意：参数值只能在AllBizParams中获取
     */
    private void setDefaultParams(BaseCell cell, Map params) throws Exception {
        String defalutParamsKey = getOptString(cell, PARAMSKEY);
        //设置默认参数值
        setParams(cell, params, defalutParamsKey);
    }

    /**
     * 设置其他参数 注意：参数值只能在AllBizParams中获取
     */
    private void setOtherParams(BaseCell cell, Map params) {
        String othrtParamsKey = getOptString(cell, OTHERPARAMSKEY);
        if (!TextUtils.isEmpty(othrtParamsKey)) {
            for (String key : othrtParamsKey.split(";")) {
                setParams(cell, params, key);
            }
        }
    }

    /**
     * 设置固定参数 固定参数必须按照json的形式写入
     * 例如：{"key":"username","value":"zhangsan"}
     */
    private void setFixParams(BaseCell cell, Map params) {
        String fixParamsKey = getOptString(cell, FIXARAMSKEY);
        if (TextUtils.isEmpty(fixParamsKey))
            return;
        try {
            JSONObject jsonObject = new JSONObject(fixParamsKey);
            String key = jsonObject.getString("key");
            String value = jsonObject.getString("value");
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                params.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setParams(BaseCell cell, Map params, String paramsKey) {
        try {
            if (!TextUtils.isEmpty(paramsKey)) {
                String value = "";
                if (cell.getAllBizParams().get(paramsKey) != null) {
                    value = cell.getAllBizParams().get(paramsKey).toString();
                }
                //参数是否需要加密
                if (getNeedEncryption(cell, paramsKey)) {
                    value = AesEncryptUtil.encrypt(value);
                }
                params.put(paramsKey, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否需要加密
     */
    private boolean getNeedEncryption(BaseCell cell, String targetKey) {
        String encryKeysStr = getOptString(cell, ENCRYPTIONKEYS);
        if (TextUtils.isEmpty(targetKey) || TextUtils.isEmpty(encryKeysStr)) {
            return false;
        } else {
            String[] encryKeys = encryKeysStr.split(";");
            return Arrays.asList(encryKeys).contains(targetKey);

        }
    }

    private boolean getBoolean(BaseCell cell, String param) {
        try {
            return "true".equals(getOptString(cell, param).toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    protected String getOptString(BaseCell cell, String key) {
        String backStr = "";
        if (cell == null || TextUtils.isEmpty(key)) {
        } else {
            backStr = cell.optStringParam(key);
            if (TextUtils.isEmpty(backStr) && cell.optJsonObjectParam(DATAS) != null) {
                backStr = cell.optJsonObjectParam(DATAS).optString(key);
            }
        }
        return backStr;
    }
}
