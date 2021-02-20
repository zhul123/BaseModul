package com.base;

import android.widget.ImageView;

import com.base.http.MyHttpUtils;
import com.base.imagehelper.ImageHelper;
import com.component.base.BaseApplication;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.util.IInnerImageSetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MyHttpUtils.init();
        TangramBuilder.init(this, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view,
                                                                 @Nullable String url) {
                ImageHelper.getInstance().setCommImage(url,view,0);
            }
        }, ImageView.class);
    }
}
