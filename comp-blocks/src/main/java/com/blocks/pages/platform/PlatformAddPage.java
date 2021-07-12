package com.blocks.pages.platform;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.utils.JsonUtils;
import com.base.utils.ScreenUtils;
import com.blocks.R;
import com.blocks.pages.platform.event.PlatformEvent;
import com.blocks.pages.platform.presenter.PlatformContract;
import com.blocks.pages.platform.presenter.PlatformPresenterImpl;
import com.blocks.pages.platform.tansform.PlatformDataUtil;
import com.blocks.pages.platform.tansform.PlatformTansform;
import com.blocks.views.commviews.bottomdialog.BottomPopDialog;
import com.blocks.views.floorcells.PlatformCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.AppListFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TabViewPagerFloorView;
import com.blocks.views.fragments.PlatformFragmentItem;
import com.component.base.BasePage;
import com.component.base.IPresenter;
import com.lib.block.style.ViewType;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 工作台添加应用page
 */
public class PlatformAddPage extends BasePage {

    private AppListFloorView mAppListFloorView;
    private View line;
    private TabViewPagerFloorView mtabView;

    public PlatformAddPage(Context context) {
        super(context);
    }

    public PlatformAddPage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlatformAddPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlatformAddPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        int padding = ScreenUtils.dipToPx(10);
        setPadding(padding, 0, padding, padding);
        mAppListFloorView = new AppListFloorView(getContext());
        mAppListFloorView.postBindView(null);
        line = new View(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(params);
        line.setBackgroundColor(getContext().getResources().getColor(R.color.default_divider));
        mtabView = new TabViewPagerFloorView(getContext(), PlatformFragmentItem.FunctionType.ADD);
        mtabView.setHeightMatch();
        addView(mAppListFloorView);
        addView(line);
        addView(mtabView);
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mAppListFloorView.refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void event(Object o) {
        super.event(o);
        if (o instanceof PlatformEvent) {
           mAppListFloorView.refresh();
        }
    }
}
