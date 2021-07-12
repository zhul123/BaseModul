package com.base.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author :  jiahongfei
 * @email : jiahongfeinew@163.com
 * @date : 2019/7/7
 * @desc : App中的Toast
 */
public class AppToast {

    private static final String TAG = AppToast.class.getSimpleName();

    private HashMap<Object, Long> map = new HashMap<Object, Long>();

    private static AppToast toast;

    private static Context context;

    private Toast mToast;

    private static final long INTERVAL = 2000;

    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        try {
            //android 7.1.1  api 25 出现的问题
            //https://www.jianshu.com/p/e6f69182107d
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            }
        } catch (Exception e) {

        }
    }

    private AppToast(Context context) {
        AppToast.context = context;
    }

    public static AppToast getInstance(Application context) {
        if (toast == null) {
            synchronized (AppToast.class) {
                if (toast == null) {
                    toast = new AppToast(context);
                }
            }
        }
        return toast;
    }

    private static void hook(Toast toast) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                    && null != sField_TN
                    && null != sField_TN_Handler) {
                Object tn = sField_TN.get(toast);
                Handler preHandler = (Handler) sField_TN_Handler.get(tn);
                sField_TN_Handler.set(tn, new SafeHandler(preHandler));
            }
        } catch (Exception e) {
        }
    }


    private static class SafeHandler extends Handler {
        private Handler impl;

        public SafeHandler(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }

    /**
     * Show toast{@link Toast#LENGTH_SHORT}
     *
     * @param res 　text resourceId
     * @param
     */
    public void makeText(int res) {
        if (null == context) {
            return;
        }
        try {
            makeText(context.getString(res), Toast.LENGTH_SHORT);
        }catch (Exception e){

        }
    }

    /**
     * Show toast{@link Toast#LENGTH_SHORT}
     *
     * @param str been show。
     * @param
     */
    public void makeText(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                makeText(str, Toast.LENGTH_SHORT);
            }catch (Exception e){

            }
        } else {
            Log.d(TAG, "makeText str is null");
        }

    }

    /**
     * Show toast{@link Toast#LENGTH_SHORT, Toast#LENGTH_LONG}
     *
     * @param str been show。
     * @param
     */
    public void makeTextOut(String str, int duration) {
        if (!TextUtils.isEmpty(str)) {
            try {
                makeText(str, duration);
            }catch (Exception e){

            }
        } else {
            Log.d(TAG, "makeText str is null");
        }
    }

    public void show(){
        if(null != toast){
            toast.show();
        }else{
            Log.d(TAG, "toast object is null, do nothing");
        }
    }

    public void cancelToast() {
        if (null == mToast) {
            Log.d(TAG, "toast object is null, do nothing");
        } else {
            mToast.cancel();
//            AppXToastUtil.cancelToast();
        }
    }

    /**
     * Show toast with custom type
     *
     * @param str
     * @param type
     */
    private void makeText(String str, int type) throws Exception {
        if (null == context) {
            return;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                // need not show toast if host activity is finish
                return;
            }
        }

        if (map.get(str) == null
                || System.currentTimeMillis() - map.get(str) > INTERVAL) {
            if (null == mToast) {
                mToast = Toast.makeText(context, str, type);
                hook(mToast);
            } else {
                mToast.setText(str);
            }
            mToast.show();
//            AppXToastUtil.setToastTime(type);
//            AppXToastUtil.showToast(str);
            map.put(str, System.currentTimeMillis());
        }
    }

}
