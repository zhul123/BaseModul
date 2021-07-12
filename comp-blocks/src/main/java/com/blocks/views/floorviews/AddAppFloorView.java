package com.blocks.views.floorviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.base.imagehelper.ImageHelper;
import com.base.utils.PageUtil;
import com.base.utils.ScreenUtils;
import com.base.utils.savedata.DataUtil;
import com.blocks.R;
import com.blocks.Utils;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.views.base.BaseFloorView;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.eventbus.BusSupport;
import com.tmall.wireless.tangram.eventbus.Event;
import com.tmall.wireless.tangram.eventbus.EventContext;
import com.tmall.wireless.tangram.eventbus.EventHandlerWrapper;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * 添加应用列表
 * 应用名称 appName
 * 应用描述 appDesc
 * 图片url imgUrl
 */
public class AddAppFloorView extends BaseFloorView implements View.OnClickListener {
    private static final String APPNAME = "appName";
    private static final String APPDESC = "appDesc";
    private static final String LOGOURL = "logoUrl";
    private static final String ISCUSTOM = "移除";
    private static final String NOTCUSTOM = "添加";
    TextView mTitleName;
    TextView mDesc;
    TextView mBtn;
    ImageView mImageView;
    protected BusSupport mBusSupport;
//    protected EventHandlerWrapper mEventHandlerWrapper;

    private Drawable blueDrawable,grayDrawable;
    private int blackColor,blueColor;

    public AddAppFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
//        if(getContext() instanceof FragmentActivity) {
//            model = new ViewModelProvider((FragmentActivity) getContext(), new ViewModelProvider.NewInstanceFactory()).get(PlatformDataUtil.class);
//        }

        View main = LayoutInflater.from(getContext()).inflate(R.layout.floor_app_add_item,null);
        mBtn = main.findViewById(R.id.tv_btn);
        mDesc = main.findViewById(R.id.tv_desc);
        mTitleName = main.findViewById(R.id.tv_title);
        mImageView = main.findViewById(R.id.img_app);
        addView(main,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        initResources();
    }
    @Override
    public void cellInited(BaseCell cell) {
        super.cellInited(cell);
        mBusSupport = cell.serviceManager.getService(BusSupport.class);
//        mEventHandlerWrapper = BusSupport.wrapEventHandler(EVENTTYPE, "", this, "busSupportEvent");
    }

    @Override
    protected void setCustomStyle() {

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
//        mBusSupport.register(mEventHandlerWrapper);
        List<PlatformBean> customList = PlatformDataUtil.getInstance().getCustomDatas();
        boolean isCustom = false;
        for(PlatformBean bean : customList){
            if(getOptString(APPNAME).equals(bean.getAppName())){
                isCustom = true;
                break;
            }
        }
        changeBtnStatus(isCustom);
        mTitleName.setText(getOptString(APPNAME));
        mDesc.setText(getOptString(APPNAME));
        ImageHelper.getInstance().setRadiusDrawable(mImageView,getOptString(LOGOURL),getOptInt(StyleEntity.RADIUS));
        mBtn.setOnClickListener(this);
    }

    private void changeBtnStatus(boolean isCustom){
        mBtn.setBackground(isCustom ? grayDrawable : blueDrawable);
        mBtn.setTextColor(isCustom ? blackColor : blueColor);
        mBtn.setText(isCustom ? ISCUSTOM : NOTCUSTOM);
        mBtn.setTag(isCustom);
    }

    @Override
    public void postUnBindView(BaseCell cell) {
//        mBusSupport.unregister(mEventHandlerWrapper);
    }

    private void initResources(){
        Resources resources = getContext().getResources();
        blackColor = resources.getColor(R.color.default_black);
        blueColor = resources.getColor(R.color.default_blue);
        grayDrawable = resources.getDrawable(R.drawable.floor_bg_radius_gray);
        blueDrawable = resources.getDrawable(R.drawable.floor_bg_radius_blue);
    }

    @Override
    public void onClick(View view) {
        if(PageUtil.getInstance().isDouble()){
            return;
        }
        String beanStr = mBaseCell.extras.optString(Params.DATAS);
        try {
            PlatformBean bean = JSON.parseObject(beanStr, PlatformBean.class);
            PlatformEvent event = new PlatformEvent();
            event.bean = bean;
            event.cell = mBaseCell;
            //tag：是否为常用应用
            boolean isCustom = (boolean) mBtn.getTag();
            if (isCustom) {
                PlatformDataUtil.getInstance().removeCustomDatas(bean);
                event.eventType = PlatformEvent.EventType.DEL;
                EventBus.getDefault().post(event);
            }else{
                PlatformDataUtil.getInstance().addCustomDatas(bean);
                event.eventType = PlatformEvent.EventType.ADD;
                EventBus.getDefault().post(event);
            }
            changeBtnStatus(!isCustom);
        }catch (Exception e){

        }
    }

    public void busSupportEvent(Event e){

    }
}
