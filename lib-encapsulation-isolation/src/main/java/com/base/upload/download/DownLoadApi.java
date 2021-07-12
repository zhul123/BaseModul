package com.base.upload.download;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhangxiaowen on 2019/2/19.
 */
public interface DownLoadApi {

    //直接使用网址下载
    @Streaming
    @GET
    Flowable<ResponseBody> download(@Url String url);
}
