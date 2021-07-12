package com.base.upload.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by zhangxiaowen on 2019/2/19.
 */
public class ProgressInterceptor implements Interceptor {

    private DownLoadSubscriberCallBack mDownLoadSubscriberCallBack;

    public ProgressInterceptor() {
    }

    public ProgressInterceptor(DownLoadSubscriberCallBack downLoadSubscriberCallBack) {
        this.mDownLoadSubscriberCallBack = downLoadSubscriberCallBack;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), mDownLoadSubscriberCallBack))
                .build();
    }
}
