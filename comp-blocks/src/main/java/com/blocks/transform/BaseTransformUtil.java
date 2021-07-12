package com.blocks.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blocks.views.utils.ParamsUtils;
import com.lib.block.style.Params;


public class BaseTransformUtil{
    private static String TAG = BaseTransformUtil.class.getSimpleName();
    /*
    protected JSONArray getItemsJsonArray(JSONObject itemsJsonObject, JSONArray jsonArray) throws JSONException{
        return getJsonArray(itemsJsonObject,ParamsUtils.ITEMS,jsonArray);
    }*/

    /*protected JSONArray getJsonArray(JSONArray jsonArray, String key,JSONObject jsonObject) throws JSONException{
        if(jsonArray != null){
            if(itemsJsonObject.get(key) != null) {
                itemsJsonObject.getJSONArray(key)
                        .addAll(jsonArray);
            }else{
                itemsJsonObject.put(key,jsonArray);
            }
            return itemsJsonObject;
        }else {
            return (JSONObject) new JSONObject().put(key, jsonArray);
        }
    }*/

    protected JSONObject getDatasObject(JSONObject datasJson){
        return getJsonObject(Params.DATAS, datasJson);
    }

    protected JSONObject getStyleObject(JSONObject styleJson){
        return getJsonObject(Params.STYLE,styleJson);
    }

    protected JSONObject getJsonObject(String key, Object object){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key,object);
            return jsonObject;
    }
}
