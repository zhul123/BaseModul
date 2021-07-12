package com.base.upload.download;

import com.base.http.JsonArrayConverterFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by zhangxiaowen on 2019/2/19.
 */
public class RetrofitDownUtil {

    private static volatile RetrofitDownUtil instance = null;
    private Retrofit retrofit;

    private RetrofitDownUtil() {
    }

    /**
     * 静态内部类
     */
    public static RetrofitDownUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitDownUtil.class) {
                if (instance == null) {
                    instance = new RetrofitDownUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 得到Retrofit的对象
     *
     * @return
     */
    public DownLoadApi getRetrofit(String baseUrl, DownLoadSubscriberCallBack<ResponseBody> downLoadSubscriberCallBack) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(genericClient(downLoadSubscriberCallBack))
                //2.自定义ConverterFactory处理异常情况
                .addConverterFactory(JsonArrayConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(DownLoadApi.class);
    }

    public OkHttpClient genericClient(DownLoadSubscriberCallBack<ResponseBody> downLoadSubscriberCallBack) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);
        builder.addInterceptor(new ProgressInterceptor(downLoadSubscriberCallBack));
        return builder.build();
    }
}
