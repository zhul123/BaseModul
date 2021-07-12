package com.blocks.transform;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lib.block.entity.base.CardEntity;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.lib.block.style.ViewType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MainTransformUtil extends BaseTransformUtil{
    private static MainTransformUtil instance;

    public static MainTransformUtil getInstance(){
        if(instance == null){
            synchronized (MainTransformUtil.class){
                if(instance == null){
                    instance = new MainTransformUtil();
                }
            }
        }
        return instance;
    }

    public JSONArray transMainPage(JSONObject mainJson) throws JSONException {
        JSONArray transMainJson = new JSONArray();
        List mainList = new ArrayList();
        LinkedHashMap<String,String> userMap = new LinkedHashMap<>();
        HashMap<String,String> jkbMap = new HashMap<>();
        userMap.put("userName","姓名");
        userMap.put("mainCorpName","单位");
        userMap.put("mainOrgName","部门");
        userMap.put("zgfxdq","是否到访中高风险地区");
        userMap.put("sfmqjc","是否密接");
        userMap.put("jkb","健康宝状态");

        CardEntity cardEntity = new CardEntity();

        JSONObject oneColumnObject = new JSONObject();
        oneColumnObject.put(Params.TYPE,"container-oneColumn");
        JSONArray userJsonArray = new JSONArray();
        Iterator usetIter = userMap.keySet().iterator();
        while (usetIter.hasNext()){
            String key = (String) usetIter.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Params.TYPE,"text");
            JSONObject dataJson = new JSONObject();
            dataJson.put(Params.TEXT,userMap.get(key)+": "+mainJson.getString(key));
            jsonObject.put(Params.DATAS,dataJson);
            StyleEntity styleBlock = new StyleEntity();
            styleBlock.textColor = "#fff000";
            styleBlock.textStyle = "bold";
            Log.e("zlzl1:",JSON.toJSONString(styleBlock));
            Log.e("zlzl2:",JSON.toJSON(styleBlock).toString());
            jsonObject.put(Params.STYLE, JSON.toJSONString(styleBlock));
            Log.e("zlzl3:",jsonObject.toJSONString());
            userJsonArray.add(jsonObject);
        }

        oneColumnObject.put(Params.ITEMS,userJsonArray);
        transMainJson.add(oneColumnObject);

        JSONObject twoColumnObject = new JSONObject();
        twoColumnObject.put(Params.TYPE,"container-twoColumn");
        JSONArray jsonArray = new JSONArray();
        JSONObject btn1 = getJsonObject(Params.TYPE,ViewType.TEXTVIEW);
        JSONObject text = getJsonObject(Params.TEXT,"个人信息维护");
        text.put(Params.AROUTERURL,"/floor/tangramCommon?url=edituserinfo.json");
        btn1.put(Params.DATAS,text);
        jsonArray.add(btn1);
        JSONObject btn2 = getJsonObject(Params.TYPE,ViewType.TEXTVIEW);
        JSONObject text2 = getJsonObject(Params.TEXT,"防疫信息维护");
        btn2.put(Params.DATAS,text2);
        jsonArray.add(btn2);
        twoColumnObject.put(Params.ITEMS,jsonArray);
        transMainJson.add(twoColumnObject);

        return transMainJson;
    }
}
