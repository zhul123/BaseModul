package com.base.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * fastJson 与 json之间转换，主要解决有些三方框架只支持某种jsonobject 问题。
 * 建议使用fastjson（原因：使用方便）
 * @author zl
 */
public class JsonUtils {
    private static JsonUtils instance;

    public static JsonUtils getInstance(){
        if(instance == null){
            synchronized (JsonUtils.class){
                if(instance == null){
                    instance = new JsonUtils();
                }
            }
        }
        return instance;
    }
    /**
     * fastJson 转为 json
     * @param fastJson
     * @return
     * @throws org.json.JSONException
     */
    public org.json.JSONObject transFastJsonToJson(com.alibaba.fastjson.JSONObject fastJson) throws org.json.JSONException{
        if(fastJson == null){
            throw new org.json.JSONException("fastJson transfrom to json fail : fastJson is not null");
        }else{
            return new org.json.JSONObject(fastJson.toJSONString());
        }
    }

    public com.alibaba.fastjson.JSONObject transJsonToFastJson(org.json.JSONObject jsonObject) throws com.alibaba.fastjson.JSONException{
        if(jsonObject == null){
            throw new com.alibaba.fastjson.JSONException("json transfrom to fastJson fail : jsonObject is not null");
        }else{
            return com.alibaba.fastjson.JSON.parseObject(jsonObject.toString());
        }
    }


    public org.json.JSONArray transFastJsonArrayToJsonArray(com.alibaba.fastjson.JSONArray fastJsonArray) throws org.json.JSONException{
        if(fastJsonArray == null){
            throw new org.json.JSONException("fastJsonArray transfrom to jsonArray fail : fastJsonArray is not null");
        }else{
            return new org.json.JSONArray(fastJsonArray.toJSONString());
        }
    }


    public JSONObject setJson(String key, Object object) throws JSONException{
        return new JSONObject().put(key,object);
    }

}
