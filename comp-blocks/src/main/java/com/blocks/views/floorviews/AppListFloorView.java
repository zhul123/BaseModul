package com.blocks.views.floorviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.imagehelper.ImageHelper;
import com.base.utils.ARouterUtils;
import com.base.utils.ScreenUtils;
import com.blocks.R;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.views.base.BaseFloorView;
import com.component.providers.app.AppProvider;
import com.tmall.wireless.tangram.eventbus.BusSupport;
import com.tmall.wireless.tangram.eventbus.Event;
import com.tmall.wireless.tangram.eventbus.EventHandlerWrapper;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static com.blocks.pages.platform.event.PlatformEvent.EventType.ADD;
import static com.blocks.pages.platform.event.PlatformEvent.EventType.DEL;

/**
 * 应用列表
 * 左边文字描述 leftText 默认已添加（%s）
 * 右边文字描述 rightText 默认设置
 * 右边文字路由 arouterUrl （公参）
 * 中间图片列表 imgList（超出则显示。。。）
 */
public class AppListFloorView extends BaseFloorView implements View.OnClickListener {

    private static final String LEFTTEXT = "leftText";
    private static final String LEFTTEXTDEFAULT = "已添加(%s)";
    private static final String RIGHTTEXT = "rightText";
    private static final String RIGHTTEXTDEFAULT = "设置";
    private static final String IMGLIST = "imgList";
    LinearLayout mLinearLayout;
    TextView mLeftTextView;
    TextView mRightTextView;
    List<PlatformBean> list;

    public AppListFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        View main = LayoutInflater.from(getContext()).inflate(R.layout.floor_app_list,null);
        mLinearLayout = main.findViewById(R.id.ll_appList);
        mLeftTextView = main.findViewById(R.id.tv_left);
        mRightTextView = main.findViewById(R.id.tv_right);
        addView(main,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void setCustomStyle() {

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        initDatas();
        setOnClickListener(this);
    }

    private void initDatas(){
        list = PlatformDataUtil.getInstance().getCustomDatas();
        int total = list.size();
        mLeftTextView.setText(String.format(LEFTTEXTDEFAULT,total));
        mRightTextView.setVisibility(total == 0 ? GONE : VISIBLE);
        mLinearLayout.post(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int total = list.size();
            int height = ScreenUtils.dipToPx(getContext(),24);
            int margin = ScreenUtils.dipToPx(getContext(),6);
            int maxSize = mLinearLayout.getWidth()/(height + margin);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height,height);
            params.leftMargin = margin;
            mLinearLayout.removeAllViews();
            int showTotal = total > maxSize ? maxSize - 1 : total;
            for(int i = 0 ; i< showTotal;i++){
                PlatformBean bean = list.get(i);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                ImageHelper.getInstance().setRadiusDrawable(imageView,bean.logoUrl,5);
                mLinearLayout.addView(imageView);
            }
            if(total > maxSize){
                ImageView moreImageView = new ImageView(getContext());
                int padding = ScreenUtils.dipToPx(getContext(),2);
                moreImageView.setLayoutParams(params);
                moreImageView.setImageResource(R.drawable.icon_more);
                moreImageView.setBackgroundResource(R.drawable.floor_bg_radius_5);
                moreImageView.setPadding(padding,padding,padding,padding);
                mLinearLayout.addView(moreImageView);
            }
        }
    };

    @Override
    public void postUnBindView(BaseCell cell) {
        if(mLinearLayout != null){
            mLinearLayout.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onClick(View view) {
        if(list.size() > 0) {
            ARouterUtils.goNext(AppProvider.Path.PATH_PLATFORM_SET);
        }
    }

    public void refresh(){
        initDatas();
    }
}
