package com.blocks.pages.platform.tansform;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.base.utils.savedata.DataUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlatformDataUtil extends ViewModel {
    private static String PLATFORMALLDATAS = "platformalldatas";
    private static String PLATFORMHISTORYDATAS = "platformhistorydatas";
    private static String PLATFORMCUSTOMDATAS = "platformCustomdatas";
    private static int MAXHISTORY = 20;
    private static PlatformDataUtil instancce = null;
    private List<PlatformBean> allDatas;
    private List<PlatformBean> historyDatas;
    private List<PlatformBean> customDatas;
    public static PlatformDataUtil getInstance(){
        if(instancce == null){
            synchronized (PlatformDataUtil.class){
                if(instancce == null){
                    instancce = new PlatformDataUtil();
                }
            }
        }
        return instancce;
    }

    public void saveAllDatas(String result){
        if(TextUtils.isEmpty(result))
            return;
        PlatformList datas = JSONObject.parseObject(result, PlatformList.class);
        allDatas = datas.getData();
        DataUtil.getInstance().put(PLATFORMALLDATAS,JSON.toJSONString(allDatas));
    }

    public List<PlatformBean> getDatasByType (String type){
        List<PlatformBean> typeDatas = new ArrayList<>(4);
        if(TextUtils.isEmpty(type))
            return typeDatas;
        if(allDatas == null){
            try {
                allDatas = JSONObject.parseArray(DataUtil.getInstance().getString(PLATFORMALLDATAS), PlatformBean.class);
            }catch (Exception e){}
        }
        if(allDatas == null)
            return typeDatas;
        for(PlatformBean platformBean : allDatas){
            if(type.equals(platformBean.appType)){
                typeDatas.add(platformBean);
            }
        }
        return typeDatas;
    }

    public List<PlatformBean> getCustomDatas(){
        if(customDatas == null) {
            String customStr = DataUtil.getInstance().getString(PLATFORMCUSTOMDATAS);
            if (!TextUtils.isEmpty(customStr)) {
                customDatas = JSONObject.parseArray(customStr, PlatformBean.class);
            } else {
                //默认固定办公服务
                customDatas = getDatasByType("00");
                if(customDatas == null) {
                    customDatas = new ArrayList<>();
                }
            }
        }
        return customDatas;
    }

    public void addCustomDatas(PlatformBean platformBean){
        if(customDatas == null) {
            getCustomDatas();
        }
        customDatas.add(platformBean);
        DataUtil.getInstance().put(PLATFORMCUSTOMDATAS,JSON.toJSONString(customDatas));
    }

    public void removeCustomDatas(PlatformBean platformBean){
        if(customDatas == null) {
            getCustomDatas();
        }
        for(PlatformBean bean : customDatas){
            if(bean.appName != null && bean.appName.equals(platformBean.appName)){
                customDatas.remove(bean);
                break;
            }
        }
        DataUtil.getInstance().put(PLATFORMCUSTOMDATAS,JSON.toJSONString(customDatas));
    }
    public boolean setCustomDatas(List<PlatformBean> platformBeans){
        if(platformBeans != null) {
            if(customDatas != null){
                customDatas.clear();
                customDatas.addAll(platformBeans);
            }else{
                customDatas = platformBeans;
            }
            return DataUtil.getInstance().put(PLATFORMCUSTOMDATAS, JSON.toJSONString(platformBeans));
        }
        return false;
    }


    public void changeHistory(PlatformBean platformBean){
        getHistoryDatas();
        if(historyDatas != null){
            if(historyDatas.size() > MAXHISTORY){
                historyDatas.remove(historyDatas.size() - 1);
            }
            historyDatas.add(0,platformBean);
        }
        DataUtil.getInstance().put(PLATFORMHISTORYDATAS, JSON.toJSONString(historyDatas));
    }

    public void changeHistory(String jsonStr){
        getHistoryDatas();
        if(historyDatas != null){
            PlatformBean platformBean = JSON.parseObject(jsonStr,PlatformBean.class);
            for(PlatformBean p : historyDatas){
                if(p.appName != null && p.appName.equals(platformBean.appName)){
                    historyDatas.remove(p);
                    break;
                }
            }
            historyDatas.add(0,platformBean);
            /*if(historyDatas.size() > MAXHISTORY){
                historyDatas.remove(historyDatas.size() - 1);
            }*/
        }
        DataUtil.getInstance().put(PLATFORMHISTORYDATAS, JSON.toJSONString(historyDatas));
    }

    public JSONArray getHistoryArray(){
        String historyStr = DataUtil.getInstance().getString(PLATFORMHISTORYDATAS);
        return JSONArray.parseArray(historyStr);
    }

    public List<PlatformBean> getHistoryDatas(){
        String historyStr = DataUtil.getInstance().getString(PLATFORMHISTORYDATAS);
        if(!TextUtils.isEmpty(historyStr)){
            historyDatas = JSONObject.parseArray(historyStr, PlatformBean.class);
        }else{
            historyDatas = new ArrayList<>();
        }
        return historyDatas;
    }
}
