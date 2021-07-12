package com.base;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.base.utils.ARouterUtils;
import com.base.utils.savedata.DataUtil;
import com.blocks.pages.platform.tansform.PlatformBean;
import com.capinfo.statistics.FileHelper;
import com.component.base.BaseActivity;
import com.component.providers.common.BlocksProvider;
import com.component.providers.common.CommonProvider;

@Route(path = "/dda/dao")
public class MainActivity extends BaseActivity {
    @Autowired(name = "/providers/blocks")
    protected BlocksProvider mBlocksProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(TextUtils.isEmpty(AppSharedPreferencesHelper.getToken())) {
//            ARouterUtils.goNext("/floor/tangramtest");
            ARouterUtils.goNext("/app/platfrom");
        }else{

//            ARouterUtils.goNext("/app/approve");
            ARouterUtils.goNext("/app/platfrom");
//                ARouter.getInstance().build("/floor/tangramCommon")
//                        .withString("url","http://yqfk.bgosp.com/jeecg-boot/yqsb/pagShowMsg/getUserJbxx")
//                        .navigation();
        }
//        BlocksProvider provider = ARouter.getInstance().navigation(BlocksProvider.class);
        BlocksProvider provider = (BlocksProvider) ARouter.getInstance().build("/provider1/blocks").getProvider();
        if(provider != null){
            Log.e("zlpro","notnull");
            provider.getParentCards();
        }*/
       /* Log.e("zlpro11","1111"+(mBlocksProvider == null));
        AppProvider provider1 = ARouter.getInstance().navigation(AppProvider.class);
        if(provider1 != null){
            Log.e("zlpro11","notnull");
        }*/
        findViewById(R.id.tv_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouterUtils.goNext("/floor/tangramtest");
            }
        });
        findViewById(R.id.tv_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String str = FileHelper.readFileToString()
                System.out.println("======str:"+str);
            }
        });

        findViewById(R.id.tv_set).setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                i++;
                FileHelper.writeLogToSDCard(MainActivity.this,("This is a TextWord"+i).getBytes());
            }
        });
        findViewById(R.id.tv_jm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = DataUtil.getInstance().setCryptKey("zhulei").put("zl2","hfakfjkaddsfjazzz");
                System.out.println("==========="+b);
//                ARouterUtils.goNext("/app/approve");
//                ARouterUtils.goNext("/app/platfrom");
                PlatformBean bean = new PlatformBean();
                bean.appName = "ahdfkannnnnnn";
                ARouter.getInstance().build("/app/platfrom")
//                        .withString(CommonProvider.CommonConstantDef.TITLE,"aufofhaslfakl")
                        .withSerializable(CommonProvider.CommonConstantDef.TITLE,bean)
                        .navigation();
            }
        });

    }

    @Override
    protected int getlayoutId() {
        return R.layout.activity_main;
    }
}