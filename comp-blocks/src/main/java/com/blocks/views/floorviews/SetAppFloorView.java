package com.blocks.views.floorviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.base.imagehelper.ImageHelper;
import com.base.utils.ScreenUtils;
import com.base.widget.easyswipement.EasySwipeMenuLayout;
import com.base.widget.easyswipement.State;
import com.blocks.R;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.views.base.BaseFloorView;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.eventbus.BusSupport;
import com.tmall.wireless.tangram.eventbus.Event;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.base.widget.easyswipement.State.CLOSE;

/**
 * 添加应用列表
 * 应用名称 appName
 * 图片url imgUrl
 */
public class SetAppFloorView extends BaseFloorView implements View.OnClickListener {
    private static final String APPNAME = "appName";
    private static final String LOGOURL = "logoUrl";
    TextView mTitleName;
    TextView mRightBtn;
    ImageView mImageView;
    ImageView mSub;
    EasySwipeMenuLayout mEasySwipeMenuLayout;
    private boolean isClick;


    public SetAppFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        setBackgroundColor(Color.WHITE);
        View main = LayoutInflater.from(getContext()).inflate(R.layout.floor_app_set_item,null);
        mRightBtn = main.findViewById(R.id.tv_remove);
        mTitleName = main.findViewById(R.id.tv_title);
        mImageView = main.findViewById(R.id.img_app);
        mSub = main.findViewById(R.id.img_sub);
        mEasySwipeMenuLayout = main.findViewById(R.id.sml);
        mEasySwipeMenuLayout.canScroll(false);
        addView(main,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void setCustomStyle() {

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        mTitleName.setText(getOptString(APPNAME));
        ImageHelper.getInstance().setRadiusDrawable(mImageView,getOptString(LOGOURL),getOptInt(StyleEntity.RADIUS));
        mSub.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
    }


    @Override
    public void postUnBindView(BaseCell cell) {
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_remove) {
            mEasySwipeMenuLayout.handlerSwipeMenu(State.CLOSE,0);
            PlatformEvent event1 = new PlatformEvent();
            event1.eventType = PlatformEvent.EventType.DEL;
            event1.position = mBaseCell.pos;
            event1.cell = mBaseCell;
            EventBus.getDefault().post(event1);
        }else if(view.getId() == R.id.img_sub){
            mEasySwipeMenuLayout.handlerSwipeMenu(State.RIGHTOPEN);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //当点击区域落在右侧按钮展示区域，则侧滑栏，等点击事件处理完后关闭侧滑
       /* if(ev.getActionMasked() == MotionEvent.ACTION_DOWN && mEasySwipeMenuLayout.getStateCache() == State.RIGHTOPEN){
            mEasySwipeMenuLayout.closeCacheView();
            mEasySwipeMenuLayout.handlerSwipeMenu(State.CLOSE);
            int rightBtnLeft = mRightBtn.getLeft() - mRightBtn.getMeasuredWidth();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            System.out.println("========拦截Y:"+ev.getRawY());
            System.out.println("========拦截X:"+ev.getX());
            System.out.println("========拦截Top:"+ rect.top);
            System.out.println("========拦截Btn:"+ rect.bottom);
            System.out.println("========rightBtnLeft:"+rightBtnLeft);
            if(ev.getX() > rightBtnLeft && ev.getRawY() > rect.top && ev.getRawY()< rect.bottom) {

                System.out.println("========拦截成功:");
                onChildClick(mRightBtn,mBaseCell);
                return true;
            }
        }*/
        return super.dispatchTouchEvent(ev);
    }
}
