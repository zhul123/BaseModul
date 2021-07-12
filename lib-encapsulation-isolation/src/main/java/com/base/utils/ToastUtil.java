package com.base.utils;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.base.app.BaseApplication;

public class ToastUtil {

    private Context mContext;
    private static ToastUtil toast = null;

    private ToastUtil(Context context) {
        mContext = context;
    }

    public static ToastUtil getInstance() {
        if (null == toast) {
            synchronized (ToastUtil.class) {
                if (null == toast) {
                    toast = new ToastUtil(BaseApplication.getInstance());
                }
            }
        }
        return toast;
    }

    /**
     * 导致内存泄漏
     *
     * @param context
     * @return
     */
    @Deprecated
    public static ToastUtil getInstance(Context context) {
        context = BaseApplication.getInstance();
        if (toast == null) {
            toast = new ToastUtil(context);
        }
        return toast;
    }

    /**
     * Show toast{@link Toast#LENGTH_SHORT}
     *
     * @param res 　text resourceId
     * @param
     */
    public void makeText(int res) {
        AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(res);
    }

    /**
     * Show toast{@link Toast#LENGTH_SHORT}
     *
     * @param str been show。
     * @param
     */
    public void makeText(String str) {
        AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(str);
    }

    /**
     * 支持设置时长
     * @param str
     * @param duration
     */
    public void makeText(String str, int duration) {
        AppToast.getInstance((Application) mContext.getApplicationContext()).makeTextOut(str, duration);
    }

    public void cancelToast() {
        AppToast.getInstance((Application) mContext.getApplicationContext()).cancelToast();
    }
}
