package com.block.transform;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lib.block.entity.base.CardEntity;
import com.lib.block.entity.base.ChooseViewEntity;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.entity.base.ViewEntity;
import com.lib.block.style.CardType;
import com.lib.block.style.ViewType;

import java.util.ArrayList;
import java.util.List;


public class ChooseDialogTransform {

    public static JSONArray transform(String datas){
        try {
            if (TextUtils.isEmpty(datas))
                return null;
            List<ChooseViewEntity> datasList = JSONArray.parseArray(datas, ChooseViewEntity.class);
            List<ViewEntity> viewEntities = new ArrayList<>();

            StyleEntity styleEntity = new StyleEntity();
            styleEntity.setTextColor("#ff3c4e");
            styleEntity.setTextSize("16");
            for(ChooseViewEntity choose : datasList){
                ViewEntity<ChooseViewEntity> viewEntity = new ViewEntity();
                viewEntity.setType(ViewType.TEXTVIEW);
                viewEntity.setDatas(choose);
                viewEntity.setStyle(styleEntity);
                viewEntities.add(viewEntity);
            }
            String str = JSON.toJSONString(viewEntities);
            return JSONArray.parseArray(str);
        }catch (Exception e){
            return null;
        }
    }
}
