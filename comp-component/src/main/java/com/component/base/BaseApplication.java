package com.component.base;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

public abstract class BaseApplication extends MultiDexApplication {
    public static final String TEST_BOOT_TAG = "test_boot_tag";

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
        if(BuildConfig.DEBUG){
            ARouter.openDebug();
            ARouter.openLog();
        }
    }
}
