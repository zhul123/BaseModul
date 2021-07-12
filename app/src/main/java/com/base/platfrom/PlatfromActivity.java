package com.base.platfrom;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.R;
import com.base.utils.ARouterUtils;
import com.blocks.pages.platform.PlatformPage;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.component.base.BaseActivity;
import com.component.providers.app.AppProvider;
import com.component.providers.common.CommonProvider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import androidx.annotation.NonNull;

@Route(path = "/app/platfrom", name = "工作台")
public class PlatfromActivity extends BaseActivity {
    @Autowired(name = "url", desc = "加载本地数据地址")
    protected String dataUrl = "";
    @Autowired(name = CommonProvider.CommonConstantDef.TITLE, desc = "标题")
    protected PlatformBean title;
    //    protected String title1 = "";
    protected SmartRefreshLayout smartrefreshlayout;
    PlatformPage platformPage;
    private ImageView mImageViewMore;


    @Override
    protected int getlayoutId() {
        return com.blocks.R.layout.floor_activity_tangrem_platform;
    }

    @Override
    public void initView() {
        super.initView();
        smartrefreshlayout = findViewById(com.blocks.R.id.smartrefreshlayout);
        if(title !=null) {
            ((TextView) findViewById(com.blocks.R.id.tv_title)).setText(title.appName);
        }
//        platformPage = findViewById(R.id.platform);
        platformPage = new PlatformPage(this);
        smartrefreshlayout.addView(platformPage);
        mImageViewMore = findViewById(R.id.iv_more);
        mImageViewMore.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mImageViewMore.setVisibility(View.VISIBLE);
        mImageViewMore.setImageResource(R.drawable.iccon_set);
            smartrefreshlayout.setEnableLoadMore(false);
            smartrefreshlayout.setEnableRefresh(false);
            smartrefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    try {
//                        mPresenter.getCorp("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    smartrefreshlayout.finishLoadMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {

//                    mPresenter.getCorp("");
                    platformPage.onCreate();
                    smartrefreshlayout.finishRefresh();
                }
            });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.iv_more){
            ARouterUtils.goNext(AppProvider.Path.PATH_PLATFORM_SET);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
