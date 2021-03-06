package com.base.http.okhttp.builder;


import com.base.http.okhttp.OkHttpUtils;
import com.base.http.okhttp.request.OtherRequest;
import com.base.http.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
