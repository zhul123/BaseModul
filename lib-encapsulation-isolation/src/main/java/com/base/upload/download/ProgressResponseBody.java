package com.base.upload.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zhangxiaowen on 2019/2/19.
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private DownLoadSubscriberCallBack mDownLoadSubscriberCallBack;

    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, DownLoadSubscriberCallBack downLoadSubscriberCallBack) {
        this.responseBody = responseBody;
        this.mDownLoadSubscriberCallBack = downLoadSubscriberCallBack;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                mDownLoadSubscriberCallBack.onProgressChange(bytesReaded, contentLength());
                return bytesRead;
            }
        };
    }
}
