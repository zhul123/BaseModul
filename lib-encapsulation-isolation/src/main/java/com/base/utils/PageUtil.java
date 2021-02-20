package com.base.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.ContextThemeWrapper;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;

/**
 * @author zhulei
 * @date 2021.01.01
 */
public class PageUtil {
    /**
     * 判断当前activity是否还活着
     *
     * @return
     */
    public static boolean isLive(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (activity.isFinishing()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLive(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return isLive(activity);
        } else {
            return false;
        }
    }

    /**
     * 判断当前activity是否还活着（带了判断是dialog的情况处理）
     * @param context
     * @return
     */
    public static boolean isLiveIncludeDialog(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return isLive(activity);
        } else {
            //Dialog的情况要特殊处理
            if (context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
                return isLive(context);
            } else {
                return false;
            }
        }
    }


   /* *//**
     * 获取给定的class的Activity的前一个Activity的SimpleName
     *
     * @param clazz 要获取的Activity的class
     * @return 如果栈中只有一个Activity或者未找到都返回空串，如果有多个返回最后一个
     *//*
    public static String getPrePageName(Class<?> clazz) {
        AppInterfaceProvider mProvider = ARouter.getInstance().navigation(AppInterfaceProvider.class);
        if (null == mProvider) {
            return "";
        }
        List activityList = mProvider.getActivityList();
        if (null == activityList || activityList.size() <= 0) {
            return "";
        }
        int target = -1;
        int listSize = activityList.size();
        for (int i = 0; i < listSize; i++) {
            Object obj = activityList.get(i);
            if (null == obj) {
                continue;
            }
            if (obj.getClass() == clazz) {
                target = i;
                break;
            }
        }
        if (-1 == target || 0 == target) {
            return "";
        }
        return activityList.get(target - 1).getClass().getSimpleName();
    }*/
}
