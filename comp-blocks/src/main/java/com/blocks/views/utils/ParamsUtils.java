package com.blocks.views.utils;

import android.text.TextUtils;
import android.util.Log;

import com.blocks.BuildConfig;
import com.blocks.views.AesEncryptUtil;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamsUtils {

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
        if(BuildConfig.DEBUG) {
            Log.e("zlzl:", params.toString());
        }
        return params;
    }

    private void checkParams(BaseCell cell) throws CheckMustException {
        if (getBoolean(cell, Params.MUSTINPUT)) {
            String param = getOptString(cell, Params.PARAMSKEY);
            Object paramObject = cell.getAllBizParams().get(param);
            if (paramObject == null  || TextUtils.isEmpty(paramObject.toString())) {
                throw new CheckMustException(getOptString(cell, Params.WARNTEXT));
            }
        }
        //是否必须选择（常用于条款阅读）
        if(getBoolean(cell,Params.MUSTCHECK)){
            if(!getBoolean(cell,Params.CHECKSTATE)){
                throw new CheckMustException(getOptString(cell, Params.WARNTEXT));
            }
        }
    }

    /**
     * 设置默认参数 注意：参数值只能在AllBizParams中获取
     */
    private void setDefaultParams(BaseCell cell, Map params) throws Exception {
        String defalutParamsKey = getOptString(cell, Params.PARAMSKEY);
        //设置默认参数值
        setParams(cell, params, defalutParamsKey);
    }

    /**
     * 设置其他参数 注意：参数值只能在AllBizParams中获取
     */
    private void setOtherParams(BaseCell cell, Map params) {
        String othrtParamsKey = getOptString(cell, Params.OTHERPARAMSKEY);
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
        String fixParamsKey = getOptString(cell, Params.FIXARAMSKEY);
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
        String encryKeysStr = getOptString(cell, Params.ENCRYPTIONKEYS);
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
            if (TextUtils.isEmpty(backStr) && cell.optJsonObjectParam(Params.DATAS) != null) {
                backStr = cell.optJsonObjectParam(Params.DATAS).optString(key);
            }
        }
        return backStr;
    }
}
