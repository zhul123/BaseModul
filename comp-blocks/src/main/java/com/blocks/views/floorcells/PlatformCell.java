package com.blocks.views.floorcells;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.base.imagehelper.ImageHelper;
import com.base.utils.ARouterUtils;
import com.base.widget.popwindows.CustomPopWindow;
import com.base.widget.popwindows.PopListAdapter;
import com.base.widget.popwindows.PopMenuWindow;
import com.base.widget.popwindows.PopWinMenuBean;
import com.blocks.R;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.utils.style.StyleUtils;
import com.component.providers.app.AppProvider;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class PlatformCell extends BaseFloorCell<ImageTextFloorView> implements PopListAdapter.OnPopMenuClick {
    private static final String IMGURL = "logoUrl";
    private static final String TEXT = "appName";
    private static final String APPID = "appId";

    private ImageView iv_img;
    private TextView tv_title;
    private List<PopWinMenuBean> mPopDatas = new ArrayList<>(4);
    private CustomPopWindow popWindow;

    @Override
    public void postBindView(@NonNull ImageTextFloorView floorView) {
        super.postBindView(floorView);
        iv_img = floorView.findViewById(R.id.iv_img);
        tv_title = floorView.findViewById(R.id.tv_title);
        if ("addApp".equals(getOptString("appType"))) {
            iv_img.setImageResource(R.drawable.icon_appadd);
        } else {
            ImageHelper.getInstance().setRadiusDrawable(iv_img, getOptString(IMGURL), getOptInt(StyleEntity.RADIUS));
        }
        tv_title.setText(getOptString(TEXT));
        StyleUtils.getInstance().setTextViewStyle(tv_title, this);
        setImgLayoutParams(iv_img);
        PlatformBean platformBean = JSON.parseObject(extras.optJSONObject(Params.DATAS).toString(), PlatformBean.class);
        floorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (platformBean != null && !"addApp".equals(platformBean.appType)) {
                    EventBus.getDefault().post(PlatformCell.this);
                } else {
                    ARouterUtils.goNext(getOptString(Params.AROUTERURL));
                }
            }
        });
        floorView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(platformBean != null && !"addApp".equals(platformBean.appType)) {
                    showPop();
                }
                return false;
            }
        });
    }

    /**
     * 判断应用是否为常用应用
     * @return
     */
    private boolean isCustom(){
        String appId = getOptString(APPID);
        String appName = getOptString(TEXT);
        for(PlatformBean functionItem : PlatformDataUtil.getInstance().getCustomDatas()) {
            if ( !TextUtils.isEmpty(appId) && TextUtils.equals(appId, functionItem.appId)) {
                return true;
            }
            if(!TextUtils.isEmpty(appName) && TextUtils.equals(appName, functionItem.appName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否来自常用模块
     * @return
     */
    private boolean isCustomModel(){
        String parentModel = "";
        try {
            parentModel = this.parent.extras.optString(Params.MODEL);
        }catch (Exception e){

        }

       if(TextUtils.equals("custom" , parentModel)){
           return true;
        }
       return false;
    }

    private void showPop() {
        mPopDatas.clear();
        boolean isCustom = isCustom();
        if(!isCustom){
            PopWinMenuBean beanAdd= new PopWinMenuBean();
            beanAdd.content = "设为常用";
            beanAdd.imgRes = R.drawable.icon_add;
            beanAdd.itemType = PopWinMenuBean.PopMenuItemType.ADD;
            mPopDatas.add(beanAdd);
        }else{
            PopWinMenuBean beanSub= new PopWinMenuBean();
            beanSub.content = "取消常用";
            beanSub.imgRes = R.drawable.icon_sub_empty;
            beanSub.itemType = PopWinMenuBean.PopMenuItemType.DEL;
            mPopDatas.add(beanSub);
        }

        if(isCustomModel()){
            PopWinMenuBean beanOrder = new PopWinMenuBean();
            beanOrder.content = "排序";
            beanOrder.imgRes = R.drawable.icon_change;
            beanOrder.itemType = PopWinMenuBean.PopMenuItemType.ORDER;
            mPopDatas.add(beanOrder);
        }

        popWindow = PopMenuWindow.getInstance()
                .setDatas(mPopDatas)
                .setOnPopMenuClick(this)
                .getPopWindow(mContext).showForAutoPotison(iv_img);
    }


    @Override
    public void onMenuClick(PopWinMenuBean.PopMenuItemType popItemType) {
        PlatformBean platformBean = JSON.parseObject(extras.optJSONObject(Params.DATAS).toString(), PlatformBean.class);
        PlatformEvent event = new PlatformEvent();
        event.cell = this;
        event.bean = platformBean;
        switch (popItemType){
            case ADD:
                PlatformDataUtil.getInstance().addCustomDatas(platformBean);
                event.eventType = PlatformEvent.EventType.ADD;
                EventBus.getDefault().post(event);
                break;
            case DEL:
                PlatformDataUtil.getInstance().removeCustomDatas(platformBean);
                event.eventType = PlatformEvent.EventType.DEL;
                EventBus.getDefault().post(event);
                break;
            case ORDER:
                ARouterUtils.goNext(AppProvider.Path.PATH_PLATFORM_SET);
                break;
        }
        if (popWindow != null) {
            popWindow.dissmiss();
        }
    }
}
