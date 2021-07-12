package com.base.upload.manger;

import android.util.Log;


import com.base.upload.download.DownLoadSubscriberCallBack;
import com.base.upload.download.RetrofitDownUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by zhangxiaowen on 2019/2/19.
 */
public class DownLoadSingleProcessManger {

    private static Disposable disposable;

    public static void loadFile(String BaseUrl, String url, final String destFileDir, final DownLoadSubscriberCallBack<ResponseBody> callBack) {
        //在主线程中更新ui
        disposable = RetrofitDownUtil.getInstance().getRetrofit(BaseUrl, callBack).download(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        saveFile(destFileDir, responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribeWith(callBack);
    }

    public static void saveFile(String destFileDir, ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[4096];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            fos = new FileOutputStream(dir);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e("saveFile", e.getMessage());
            }
        }
    }

    public static void cancleLoadFile() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    public static boolean isDisposable() {
        if (disposable != null) {
            return disposable.isDisposed();
        }
        return true;
    }
}
