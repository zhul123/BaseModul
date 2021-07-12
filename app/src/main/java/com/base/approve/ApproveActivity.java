package com.base.approve;

import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.base.approve.presenter.ApproveContract;
import com.base.approve.presenter.ApprovePresenterImpl;
import com.base.http.MyHttpUtils;
import com.base.utils.JsonUtils;
import com.base.utils.ToastUtil;
import com.blocks.clicksupport.CustomClickSupport;
import com.blocks.clicksupport.MyCardSupport;
import com.blocks.transform.MainTransformUtil;
import com.blocks.views.floorcells.PlatformCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.component.base.BaseActivity;
import com.component.providers.common.CommonProvider;
import com.lib.block.style.ViewType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.support.CardSupport;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@Route(path = "/app/approve", name = "智能审批")
public class ApproveActivity extends BaseActivity implements ApproveContract.ApproveView {
    protected RecyclerView rv_common;
    @Autowired(name = "url", desc = "加载本地数据地址")
    protected String dataUrl = "";
    @Autowired(name = CommonProvider.CommonConstantDef.TITLE, desc = "标题")
    protected String title1 = "";
    private TangramEngine engine;
    protected SmartRefreshLayout smartrefreshlayout;

    private ApproveContract.ApprovePresenter mApprovePresenter;

    @Override
    protected int getlayoutId() {
        return com.blocks.R.layout.floor_activity_tangrem_common;
    }

    @Override
    public void initView() {
        super.initView();
        rv_common = findViewById(com.blocks.R.id.rv_common);
        smartrefreshlayout = findViewById(com.blocks.R.id.smartrefreshlayout);
        ((TextView) findViewById(com.blocks.R.id.tv_title)).setText(title1);
    }

    @Override
    public void initData() {
        super.initData();
        if (rv_common != null) {
            TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(this);

            builder.registerCell(ViewType.IMAGETEXTVIEW, PlatformCell.class,ImageTextFloorView.class);
            builder.registerCell(ViewType.TEXTVIEW, TextCell.class,TextView.class);

            engine = builder.build();
            engine.addSimpleClickSupport(new CustomClickSupport());
            engine.register(CardSupport.class, new MyCardSupport());
//            CardLoadSupport.setInitialPage(1);
//            engine.addCardLoadSupport(mCardLoadSupport);
            engine.bindView(rv_common);
            smartrefreshlayout.setEnableLoadMore(false);
            smartrefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    try {
//                        getDatas(dataUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    smartrefreshlayout.finishLoadMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                    smartrefreshlayout.finishRefresh();
                }
            });

        }

        mApprovePresenter = new ApprovePresenterImpl(this);
        mApprovePresenter.getFunctionValue("","");

    }

    private void getDatas(String url) throws Exception{
        if (TextUtils.isEmpty(url))
            return;
        MyHttpUtils.get(url, null, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                if (engine != null) {
                    try {
                        JSONArray jsonArray = JsonUtils.getInstance().transFastJsonArrayToJsonArray(MainTransformUtil.getInstance().transMainPage(JSON.parseObject(result)));
                        engine.setData(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(String errMsg) {
                ToastUtil.getInstance().makeText(errMsg);
            }
        },this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
        MyHttpUtils.destroy();
    }

    @Override
    public void showView(String result) {
        try {
            com.alibaba.fastjson.JSONArray jsonArray = ApproveTansform.transform(result);
            if(engine != null){
                engine.setData(JsonUtils.getInstance().transFastJsonArrayToJsonArray(jsonArray));
            }
        }catch (Exception e){

        }
    }
}
