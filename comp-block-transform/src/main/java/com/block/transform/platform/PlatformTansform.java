package com.block.transform.platform;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class PlatformTansform {
    private static String ITEMS = "items";
    private static String DATA = "data";
    private static String DATAS = "datas";
    private static String IMGBASEURL = "http://219.232.207.218/jeecg-boot/sys/common/static/";

    private static PlatformTansform instance = null;

    public static PlatformTansform getInstance(){
        if(instance == null){
            synchronized (PlatformTansform.class){
                if(instance == null)
                    instance = new PlatformTansform();
            }
        }
        return instance;
    }

    public JSONArray transformAll(String result) {
        if (TextUtils.isEmpty(result))
            return new JSONArray();
        System.out.println("=====result:"+result);
        PlatformList datas = JSONObject.parseObject(result, PlatformList.class);
        List list = datas.data;
        PlatformBean platformBean = new PlatformBean();
        platformBean.appName = "添加应用";
        platformBean.logoUrl = "http://219.232.207.218/api/v1/h5/static/media/tianjiazhaopian.28ea2dbf.png";
        platformBean.appType = "addApp";
        list.add(platformBean);
        JSONArray resultArray = transform(list);
        resultArray.addAll(0,getHeaderObj());
        resultArray.add(getOther());
        return resultArray;
    }

    public JSONArray transform(List<PlatformBean> platformBeans){
        JSONArray transfromJsonArr = new JSONArray();
        JSONObject fixCard = getCardObj();
        JSONArray itemsFixArr = fixCard.getJSONArray(ITEMS);
        for (PlatformBean bean : platformBeans){
            itemsFixArr.add(getItemObj(bean));
        }
        fixCard.put(ITEMS,itemsFixArr);
        transfromJsonArr.add(fixCard);
        return transfromJsonArr;
    }

    private JSONArray getHeaderObj(){
        String headerObj = "[{\"type\":\"imageView\",\"imgUrl\":\"http://imtest.bgosp.com/api/v1/h5/static/media/work-table.82c1b590.png\",\"aspectRatio\":\"2.7\"},{\"type\":\"textView\",\"text\":\"机关事务热线\",\"style\":{\"margin\":\"[10,16,10,16]\",\"textSize\":\"16\",\"textStyle\":\"bold\"}},{\"type\":\"appList\"},{\"type\":\"imageView\",\"imgUrl\":\"http://imtest.bgosp.com/api/v1/h5/static/media/jiguanrexian.b7f15489.png\",\"aspectRatio\":\"6\",\"style\":{\"radius\":\"6\",\"margin\":\"[0,10,0,10]\"}}]";
        return JSONArray.parseArray(headerObj);
    }

    private JSONObject getCardObj(){
        String cardObj = "{\"type\":\"container-fourColumn\",\"style\":{\"margin\":\"[10,0,0,0]\"},\"items\":[]}";
        return JSON.parseObject(cardObj);
    }

    private JSONObject getItemObj(Object datas){
        String itemObj = "{\"type\":\"imageTextView\",\"datas\":{},\"style\":{\"gravity\":\"center\",\"margin\":\"[6,4,6,4]\",\"radius\":\"6\"}}";
        JSONObject jsonObject = JSON.parseObject(itemObj);
        jsonObject.put(DATAS,datas);
        return jsonObject;
    }

    private JSONObject getOther(){
        String otherObj = "{\"type\":\"container-oneColumn\",\"header\":{\"type\":\"textView\",\"text\":\"全部应用\",\"style\":{\"margin\":\"[10,16,0,16]\",\"textSize\":\"16\",\"textStyle\":\"bold\",\"textColor\":\"#333333\"}},\"items\":[{\"type\":\"tabbar\"}]}";
        return JSON.parseObject(otherObj);
    }
}
