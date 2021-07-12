package com.capinfo.statistics;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author :  zhulei
 * @desc : 统计配置类
 */
public class Statistics {

    private List<AbsAppender> mAbsAppenders = new ArrayList<>();
    private Context mContext;

    private Statistics(Builder builder) {
        mAbsAppenders.clear();
        mAbsAppenders.addAll(builder.absAppenders);
        mContext = builder.context;
    }

    void reportEvent(String event, String label, Map<String, String> params) {
        for (AbsAppender absAppender : mAbsAppenders) {
            absAppender.appender(mContext, event, label, params);
        }
    }

    void reportResume(Context context, Map<String, String> params) {
        for (AbsAppender absAppender : mAbsAppenders) {
            absAppender.resume(context,params);
        }
    }

    void reportPause(Context context, Map<String, String> params) {
        for (AbsAppender absAppender : mAbsAppenders) {
            absAppender.pause(context,params);
        }
    }

    public static final class Builder {

        private List<AbsAppender> absAppenders = new ArrayList<>();
        private Context context;

        public Builder addAppender(AbsAppender absAppender) {
            absAppenders.add(absAppender);
            return this;
        }

        public Statistics builder(Context context) {
            this.context = context.getApplicationContext();
            return new Statistics(this);
        }

    }
}
