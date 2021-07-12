package com.blocks.pages.platform;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.base.utils.JsonUtils;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.presenter.PlatformContract;
import com.blocks.pages.platform.presenter.PlatformPresenterImpl;
import com.blocks.pages.platform.support.PlatformClickSupport;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.pages.platform.tansform.PlatformTansform;
import com.blocks.views.floorcells.PlatformCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.AppListFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TabViewPagerFloorView;
import com.component.base.BasePage;
import com.component.base.IPresenter;
import com.lib.block.style.Params;
import com.lib.block.style.ViewType;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 工作台page
 */
public class PlatformPage extends BasePage<PlatformContract.PlatformPresenter> implements PlatformContract.PlatformView {
    private static String APP_ID = "appId";
    private RecyclerView mRecyclerView;
    private TangramEngine engine;
    private boolean isFrist = true;

    public PlatformPage(Context context) {
        super(context);
    }

    public PlatformPage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlatformPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlatformPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setTag("blocksView");
        addView(mRecyclerView);

        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(getContext());

        builder.registerCell(ViewType.IMAGETEXTVIEW, PlatformCell.class, ImageTextFloorView.class);
        builder.registerCell(ViewType.TEXTVIEW, TextCell.class, TextView.class);
        builder.registerCell(ViewType.IMAGEVIEW, ImageFloorView.class);
        builder.registerCell(ViewType.APPLIST, AppListFloorView.class);
        builder.registerCell("tabbar", TabViewPagerFloorView.class);

        engine = builder.build();
        engine.addSimpleClickSupport(new PlatformClickSupport());
        engine.bindView(mRecyclerView);
    }

    @Override
    protected IPresenter createPresenter() {
        return new PlatformPresenterImpl(this);
    }

    @Override
    public void onCreate() {
        mPresenter.getCorp("");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFrist){
            initDatas();
        }
        isFrist = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
    }

    @Override
    public void showView(String result) {
        PlatformDataUtil.getInstance().saveAllDatas(result);
        initDatas();
    }

    private void initDatas() {
        try {
            com.alibaba.fastjson.JSONArray jsonArray = PlatformTansform.getInstance().transformCustom();
            if (engine != null) {
                engine.setData(JsonUtils.getInstance().transFastJsonArrayToJsonArray(jsonArray));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void event(Object o) {
        super.event(o);
        if (o instanceof PlatformEvent) {
            PlatformEvent platformEvent = (PlatformEvent) o;
            changeCardData(platformEvent.cell, platformEvent.eventType);
        }
    }

    private List<Card> datasCardList;
    private void changeCardData(BaseCell cell, PlatformEvent.EventType eventType) {
        if (cell == null || engine == null)
            return;
        datasCardList = engine.getGroupBasicAdapter().getGroups();
        if(datasCardList == null)
            return;
        for(Card card : datasCardList){
            if(TextUtils.equals("custom",card.extras.optString(Params.MODEL))){
                switch (eventType) {
                    case ADD:
                        initDatas();
                        break;
                    case DEL:
                        boolean remove = card.removeCell(cell);
                        if(!remove){
                            for(BaseCell baseCell : card.getCells()){
                                if(baseCell.extras.optJSONObject(Params.DATAS).optString(APP_ID)
                                        .equals(cell.extras.optJSONObject(Params.DATAS).optString(APP_ID))){
                                    card.removeCell(baseCell);
                                    break;
                                }
                            }
                        }
                        break;
                }
                break;
            }
        }
    }
}
