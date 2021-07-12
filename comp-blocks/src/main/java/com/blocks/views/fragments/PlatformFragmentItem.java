package com.blocks.views.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.utils.JsonUtils;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.pages.platform.tansform.PlatformTansform;
import com.blocks.views.floorcells.PlatformCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.AddAppFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TitleBarFloorView;
import com.lib.block.style.ViewType;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class PlatformFragmentItem extends Fragment {
    public static final String DATAS = "datas";
    public static final String APPTYPE = "platformType";
    public static final String FUNCTIONTYPE = "functionType";
    private RecyclerView mRecyclerView;
    private TangramEngine engine;
    private List<PlatformBean> datasList = new ArrayList<>();
    private String type;
    FunctionType functionType = null;

    public enum FunctionType implements Serializable {
        DEFAULT,//默认展示应用列表
        ADD //添加常用app
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = new NestRecyclerView(getContext());
        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDatas();
    }

    private void initDatas() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Bundle bundle = getArguments();

        if(bundle != null) {
            functionType = (FunctionType) bundle.getSerializable(FUNCTIONTYPE);
            type = bundle.getString(APPTYPE);
        }
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(getContext());
        builder.registerCell(ViewType.IMAGEVIEW, ImageFloorView.class);
        builder.registerCell(ViewType.IMAGETEXTVIEW, PlatformCell.class, ImageTextFloorView.class);
        builder.registerCell(ViewType.TEXTVIEW, TextCell.class, TextView.class);
        builder.registerCell(ViewType.APPADD, AddAppFloorView.class);

        mRecyclerView.setTag("blocksView");
        engine = builder.build();
        engine.bindView(mRecyclerView);
        refreshData();
    }

    private void refreshData(){
        if("history".equals(type)){
            datasList = PlatformDataUtil.getInstance().getHistoryDatas();
        }else{
            datasList = PlatformDataUtil.getInstance().getDatasByType(type);
        }
        if(datasList != null) {
            try {
                JSONArray datasJson = null;
                if(functionType != null && functionType == FunctionType.ADD){
                    datasJson = JsonUtils.getInstance().transFastJsonArrayToJsonArray(PlatformTansform.getInstance().transformAddPage(datasList));
                }else {
                    datasJson = JsonUtils.getInstance().transFastJsonArrayToJsonArray(PlatformTansform.getInstance().transform(datasList));
                }
                engine.setData(datasJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("========fragmentOnresume");
        refreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeHistory(Object event){
        if("history".equals(type)){
            if(event instanceof BaseCell){
                BaseCell baseCell = (BaseCell)event;
//                PlatformBean platformBean = JSON.parseObject(baseCell.extras.toString(),PlatformBean.class);
                PlatformDataUtil.getInstance().changeHistory(baseCell.extras.optJSONObject("datas").toString());
                try {
                    datasList = PlatformDataUtil.getInstance().getHistoryDatas();
                    JSONArray datasJson = JsonUtils.getInstance().transFastJsonArrayToJsonArray(PlatformTansform.getInstance().transform(datasList));
                    engine.setData(datasJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
        EventBus.getDefault().unregister(this);
    }

}
