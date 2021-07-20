package com.base.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.base.widget.WaterMarkView;

import androidx.annotation.NonNull;

public class SafeUtils {
    //添加水印
    public static void setWaterMark(Activity activity, @NonNull WaterMarkView.MarkType markType, @NonNull String markText) {
        if (activity != null) {
            final ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            //可对水印布局进行初始化操作
            WaterMarkView waterMarkView = new WaterMarkView(activity);
            waterMarkView.setDraw(markType,markText);
            rootView.addView(waterMarkView);
        }
    }

    /**
     * 禁止截屏
     *
     * @param activity
     */
    public static void noScreenShot(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
