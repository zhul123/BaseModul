package com.base.okpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by jiahongfei on 2018/1/12.
 */

public class OKPermission {

    public static final int REQUEST_CODE_PERMISSION = 0;

    /**
     * true全部授权，false没有全部授权
     *
     * @return
     */
    public static boolean checkPermission(Context context, PermissionItem[] permissions) {
        List<String> noPermission = requestPermission(context, permissions);
        return noPermission.size() <= 0;
    }

    public static List<String> requestPermission(Context context, PermissionItem[] permissions) {
        List<String> requestPermission = new ArrayList<>();
        if (null == permissions || 0 == permissions.length) {
            return requestPermission;
        }
        if (context == null) {
            //此处判断，否则下面ContextCompat.checkSelfPermission会偶现崩溃（造成context为null的原因：大多是因为fragment通过getContext()获取，在根源上解决即可，用Activitiy的mContext即可）
            return requestPermission;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i].permission;
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                //没有授权
                requestPermission.add(permission);
            }
        }
        return requestPermission;
    }

    public static void okPermission(Activity activity, List<String> requestPermission) {

        if (requestPermission.size() > 0) {
            ActivityCompat.requestPermissions(activity,
                    requestPermission.toArray(new String[requestPermission.size()]),
                    REQUEST_CODE_PERMISSION);
        }
    }

}
