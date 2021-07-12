package com.blocks.views.base;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tmall.wireless.tangram3.TangramEngine;
import com.tmall.wireless.tangram3.dataparser.DataParser;
import com.tmall.wireless.tangram3.dataparser.IAdapterBuilder;
import com.tmall.wireless.tangram3.dataparser.concrete.Card;
import com.tmall.wireless.tangram3.structure.BaseCell;

import androidx.annotation.NonNull;

public class MyTangramEngine extends TangramEngine {
    public MyTangramEngine(@NonNull Context context, @NonNull DataParser<JSONObject, JSONArray> dataParser, @NonNull IAdapterBuilder<Card, BaseCell> adapterBuilder) {
        super(context, dataParser, adapterBuilder);
    }

    public void removeItem(BaseCell data){
        removeBy(data);
    }

    public void removeItem(int position){
        removeBy(position);
    }
}
