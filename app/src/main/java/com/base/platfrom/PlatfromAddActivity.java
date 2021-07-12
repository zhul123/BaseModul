package com.base.platfrom;

import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.R;
import com.blocks.pages.platform.PlatformAddPage;
import com.blocks.pages.platform.PlatformPage;
import com.component.base.BaseActivity;
import com.component.providers.app.AppProvider;
import com.component.providers.common.CommonProvider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import androidx.annotation.NonNull;

@Route(path = AppProvider.Path.PATH_PLATFORM_ADD, name = "应用管理")
public class PlatfromAddActivity extends BaseActivity{
    @Autowired(name = "url", desc = "加载本地数据地址")
    protected String dataUrl = "";
    @Autowired(name = CommonProvider.CommonConstantDef.TITLE, desc = "标题")
    protected String title = "";
    private  TextView mTextView;
    private PlatformAddPage mAddPage;


    @Override
    protected int getlayoutId() {
        return R.layout.floor_activity_tangrem_platform_add;
    }

    @Override
    public void initView() {
        super.initView();
        mTextView = findViewById(R.id.tv_title);
        mAddPage = findViewById(R.id.v_addPage);
    }

    @Override
    public void initData() {
        super.initData();
        mTextView.setText("添加常用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
