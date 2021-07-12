package com.xylink.sdk.sample.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.base.app.BaseApplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.util.List;

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Note::::: 由于android机型较多, 如果应用拉不动前台, 自己适配
     *
     * @param activity
     */
    public static void moveTaskToFront(Activity activity) {
            ActivityManager activtyManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos){
                System.out.println("===========moveTash:"+runningTaskInfo.topActivity.getPackageName());
                if (activity.getPackageName().equals(runningTaskInfo.topActivity.getPackageName()))
                {
                    activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                    return;
                }
            }
    }

    public static void goHome(Activity activity) {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setAction(Intent.ACTION_MAIN);
        activity.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName()) && (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return true;
            }
        }
        return false;
    }
}
