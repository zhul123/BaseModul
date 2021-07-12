package com.blocks;

import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.base.http.MyHttpUtils;
import com.base.utils.JsonUtils;
import com.blocks.clicksupport.CustomClickSupport;
import com.blocks.clicksupport.MyCardSupport;
import com.blocks.transform.MainTransformUtil;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.floorviews.DividerFloorView;
import com.blocks.views.floorviews.EditFloorView;
import com.blocks.views.floorviews.FooterFloorView;
import com.blocks.views.floorviews.HeaderFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TitleBarFloorView;
import com.component.base.BaseActivity;
import com.component.providers.common.CommonProvider;
import com.base.utils.ToastUtil;
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

@Route(path = "/floor/tangramCommon", name = "七巧板公共页面")
public class TangramCommActivity extends BaseActivity {
    protected RecyclerView rv_common;
    @Autowired(name = "url", desc = "加载本地数据地址")
    protected String dataUrl = "";
    @Autowired(name = CommonProvider.CommonConstantDef.TITLE, desc = "标题")
    protected String title1 = "";
    private TangramEngine engine;
    protected SmartRefreshLayout smartrefreshlayout;

    @Override
    protected int getlayoutId() {
        return R.layout.floor_activity_tangrem_common;
    }

    @Override
    public void initView() {
        super.initView();
        rv_common = findViewById(R.id.rv_common);
        smartrefreshlayout = findViewById(R.id.smartrefreshlayout);
        ((TextView) findViewById(R.id.tv_title)).setText(title1);
    }

    @Override
    public void initData() {
        super.initData();
        if (rv_common != null) {
            TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(this);

            builder.registerCell("InterfaceCell", CustomInterfaceView.class);
            builder.registerCell("AnnotationCell", CustomAnnotationView.class);
            builder.registerCell("AppModuleView", ImageTextFloorView.class);
            builder.registerCell("header", HeaderFloorView.class);
            builder.registerCell("footer", FooterFloorView.class);
            builder.registerCell("divider", DividerFloorView.class);
            builder.registerCell("imageFloor", ImageFloorView.class);
            builder.registerCell("imageView", ImageFloorView.class);
            builder.registerCell("title", TitleBarFloorView.class);
            builder.registerCell("text", TextCell.class,TextView.class);
            builder.registerCell("textView", TextCell.class,TextView.class);
            builder.registerCell("button", ButtonFloorView.class);
            builder.registerCell("edit", EditFloorView.class);

            engine = builder.build();
            engine.addSimpleClickSupport(new CustomClickSupport());
            engine.register(CardSupport.class, new MyCardSupport());
//            CardLoadSupport.setInitialPage(1);
//            engine.addCardLoadSupport(mCardLoadSupport);
            engine.bindView(rv_common);

                if (TextUtils.isEmpty(dataUrl)){
                    dataUrl = "http://yqfk.bgosp.com/jeecg-boot/yqsb/pagShowMsg/getUserJbxx";
                }


            try {
                byte[] bytes = Utils.getAssetsFile(this, dataUrl);
                if (bytes != null) {
                    String json = new String(bytes);
                    try {
                        JSONArray d = new JSONArray(json);
                        engine.setData(d);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    getDatas(dataUrl);
                }
            } catch (Exception e) {
            }

            smartrefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    try {
                        getDatas(dataUrl);
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

//        showLoadingView();
        /*rv_common.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingView();
                byte[] bytes = Utils.getAssetsFile(getBaseContext(), "spDataRefresh.json");
                if (bytes != null) {
                    String json = new String(bytes);
                    try {
                        JSONArray d = new JSONArray(json);
                        engine.appendData(d);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 2000);*/

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
}
