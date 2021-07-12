package com.base.upload.download;

import android.text.TextUtils;

import com.base.common.ConstantNet;
import com.base.http.bean.ResultException;
import com.base.upload.utils.NetWorkAvailableUtils;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by wen on 2018/5/14.
 * 自定义DisposableSubscriber(业务相关)
 * 采用适配器模式，去除不必要的接口方法
 * 关于文件上传单独写UploadSubscriberCallBack回调和统一处理区别并去除耦合
 */
public abstract class DownLoadSubscriberCallBack<T> extends DisposableSubscriber<T> {


    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        //在这里做全局的错误处理
        if (e instanceof HttpException ||
                e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException ||
                e instanceof UnknownHostException) {
            //网络错误
            onFailure(new Throwable(ConstantNet.NET_ERROR));
        } else if (e instanceof ResultException) {
            onFailure(e);
        } else {
            //其他错误
            if (!NetWorkAvailableUtils.isNetworkAvailable()) {
                //无网络连接提示文案
                onFailure(new ResultException(ConstantNet.NET_ERROR_CODE_NO_NETWORK, ConstantNet.NET_ERROR));
            } else {
                //其他错误
                String errorMsg = e.getMessage();
                onFailure(new Throwable(TextUtils.isEmpty(errorMsg) ? ConstantNet.NOT_KNOW_ERROR : errorMsg));
            }
        }
    }

    //监听文件进度的改变
    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress(bytesWritten, contentLength);
    }

    //上传进度回调
    public abstract void onProgress(long bytesWritten, long contentLength);

    @Override
    public void onComplete() {
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(Throwable t);
}
