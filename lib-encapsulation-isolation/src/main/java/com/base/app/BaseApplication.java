package com.base.app;

import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.base.http.MyHttpUtils;
import com.base.imagehelper.ImageHelper;
import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.util.IInnerImageSetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

public abstract class BaseApplication extends MultiDexApplication implements MMKVHandler {
    public static final String TEST_BOOT_TAG = "test_boot_tag";

    private static BaseApplication mInstance = null;

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        mInstance = this;
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    private void init(){
        initArouter();
        initTangram();
        MyHttpUtils.init();
        MMKV.initialize(this);
    }

    /**
     * 七巧板框架初始化，对应model为block
     */
    private void initTangram(){
        TangramBuilder.init(this, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view,
                                                                 @Nullable String url) {
                ImageHelper.getInstance().setCommImage(url,view,0);
            }
        }, ImageView.class);
    }

    /**
     * 初始化路由
     */
    private void initArouter(){
        ARouter.init(this);
        if(BuildConfig.DEBUG){
            ARouter.openDebug();
            ARouter.openLog();
        }
    }


    @Override
    public boolean wantLogRedirecting() {
        return true;
    }

    @Override
    public void mmkvLog(MMKVLogLevel level, String file, int line, String func, String message) {
        String log = "<" + file + ":" + line + "::" + func + "> " + message;
        switch (level) {
            case LevelDebug:
                //Log.d("redirect logging MMKV", log);
                break;
            case LevelInfo:
                //Log.i("redirect logging MMKV", log);
                break;
            case LevelWarning:
                //Log.w("redirect logging MMKV", log);
                break;
            case LevelError:
                //Log.e("redirect logging MMKV", log);
                break;
            case LevelNone:
                //Log.e("redirect logging MMKV", log);
                break;
        }
    }

    @Override
    public MMKVRecoverStrategic onMMKVCRCCheckFail(String s) {
        return null;
    }

    @Override
    public MMKVRecoverStrategic onMMKVFileLengthError(String s) {
        return null;
    }
}
