package com.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.component.base.BaseActivity;
import com.component.providers.app.AppProvider;
import com.component.providers.common.BlocksProvider;

@Route(path = "/dda/dao")
public class MainActivity extends BaseActivity {
    @Autowired(name = "/providers/blocks")
    protected BlocksProvider mBlocksProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().build("/floor/tangramtest").navigation();
//        BlocksProvider provider = ARouter.getInstance().navigation(BlocksProvider.class);
        BlocksProvider provider = (BlocksProvider) ARouter.getInstance().build("/provider1/blocks").getProvider();
        if(provider != null){
            Log.e("zlpro","notnull");
            provider.getParentCards();
        }
        Log.e("zlpro11","1111"+(mBlocksProvider == null));
        AppProvider provider1 = ARouter.getInstance().navigation(AppProvider.class);
        if(provider1 != null){
            Log.e("zlpro11","notnull");
        }
        findViewById(R.id.tv_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/floor/tangramtest").navigation();
            }
        });
    }

    @Override
    protected int getlayoutId() {
        return R.layout.activity_main;
    }
}