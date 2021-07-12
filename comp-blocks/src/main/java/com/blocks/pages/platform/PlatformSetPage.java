package com.blocks.pages.platform;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.base.utils.JsonUtils;
import com.base.utils.ToastUtil;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.pages.platform.tansform.PlatformTansform;
import com.blocks.views.floorviews.SetAppFloorView;
import com.component.base.BasePage;
import com.component.base.IPresenter;
import com.lib.block.style.Params;
import com.lib.block.style.ViewType;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 工作台设置page
 */
public class PlatformSetPage extends BasePage {

    private RecyclerView mRecyclerView;
    private TangramEngine engine;
    private List<PlatformBean> datasList = new ArrayList<>();
    private List<Card> datasCardList;
    private List<BaseCell> mBaseCellList;
    private ItemTouchHelper mItemTouchHelper;
    private boolean haveChange;

    public PlatformSetPage(Context context) {
        super(context);
    }

    public PlatformSetPage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlatformSetPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlatformSetPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        mRecyclerView = new RecyclerView(getContext());
        addView(mRecyclerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(getContext());

        builder.registerCell(ViewType.APPSET, SetAppFloorView.class);
        engine = builder.build();
//        engine.addSimpleClickSupport(new PlatformClickSupport());

        engine.bindView(mRecyclerView);

        datasList = PlatformDataUtil.getInstance().getCustomDatas();
        initData();
        mBaseCellList = (List<BaseCell>) engine.getGroupBasicAdapter().getComponents();
        mItemTouchHelper = new ItemTouchHelper(new MyItemTouchCallBack());
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        if(getContext() instanceof FragmentActivity) {
            PlatformDataUtil model = new ViewModelProvider((FragmentActivity) getContext(),new ViewModelProvider.NewInstanceFactory()).get(PlatformDataUtil.class);

            // Create the observer which updates the UI.
            final Observer<List<PlatformBean>> datasObserver = new Observer<List<PlatformBean>>() {
                @Override
                public void onChanged(@Nullable final List<PlatformBean> datas) {
                    // Update the UI, in this case, a TextView.
                    datasList.clear();
                    datasList.addAll(datas);
                    initData();
                }
            };

            // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//            model.getCustomDatas().observe((FragmentActivity) getContext(), datasObserver);
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    private void initData() {
        try {
            com.alibaba.fastjson.JSONArray jsonArray = PlatformTansform.getInstance().transformSetPage(datasList);
            if (engine != null) {
                engine.setData(JsonUtils.getInstance().transFastJsonArrayToJsonArray(jsonArray));
            }
        } catch (Exception e) {
        }
    }

    private void changeCardData(BaseCell cell, PlatformEvent.EventType eventType) {
        if (cell == null)
            return;
        haveChange = true;
        datasCardList = engine.getGroupBasicAdapter().getGroups();

        if (datasCardList != null && datasCardList.get(0) != null && datasCardList.get(0).getCells() != null) {
            switch (eventType) {
                case ADD:
                    datasCardList.get(0).addCell(cell);
                    mBaseCellList.add(cell);
                    break;
                case DEL:
                    datasCardList.get(0).removeCell(cell);
                    for(BaseCell cell1 : mBaseCellList){
                        PlatformBean bean1 = JSON.parseObject(cell1.extras.optString(Params.DATAS),PlatformBean.class);
                        PlatformBean bean = JSON.parseObject(cell.extras.optString(Params.DATAS),PlatformBean.class);
                        if(bean.appId.equals(bean1.appId)){
                            mBaseCellList.remove(cell1);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
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

    /**
     * 判断是否有修改
     *
     * @return
     */
    public boolean haveChange() {
        return haveChange;
    }

    public void save() {
        if (!haveChange()) {
            ((Activity) getContext()).finish();
            return;
        }
        try {
            /*String datasJson = mBaseCellList.get(0).extras.optString(Params.ITEMS);
            List<ViewEntity<PlatformBean>> viewEntities = JSON.parseObject(datasJson, new TypeReference<List<ViewEntity<PlatformBean>>>(PlatformBean.class) {
            });
            for (ViewEntity<PlatformBean> viewEntity : viewEntities) {
                customList.add(viewEntity.getDatas());
            }
             */

            List<PlatformBean> customList = new ArrayList<>();
            if(mBaseCellList != null){
                for(BaseCell cell : mBaseCellList) {
                    customList.add(JSON.parseObject(cell.optStringParam(Params.DATAS),PlatformBean.class));
                }
            }
            boolean isSuc = PlatformDataUtil.getInstance().setCustomDatas(customList);
            if(isSuc) {
                ((Activity) getContext()).finish();
            }else{
                ToastUtil.getInstance().makeText("保存失败");
            }
        }catch (Exception e){
            ToastUtil.getInstance().makeText("保存失败");
        }
    }

    private class MyItemTouchCallBack extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mBaseCellList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mBaseCellList, i, i - 1);
                }
            }
            if (fromPosition != toPosition) {
                haveChange = true;
                mRecyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            System.out.println("========onSwiped:");
        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            System.out.println("============onSelectedChanged:" + actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
