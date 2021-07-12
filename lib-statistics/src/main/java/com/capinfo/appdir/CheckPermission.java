package com.capinfo.appdir;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by zhulei
 */
public class CheckPermission {

    /**
     * Determines if the context calling has the required permission
     *
     * @param context    - the IPC context
     * @param permission - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public static boolean hasPermission(Context context, String permission) {

        PackageManager pm = context.getPackageManager();
        boolean flag = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permission, context.getPackageName()));
        return flag;
    }

    /**
     * Determines if the context calling has the required permissions
     *
     * @param context     - the IPC context
     * @param permissions - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public static boolean hasPermissions(Context context, String... permissions) {

        boolean hasAllPermissions = true;

        for (String permission : permissions) {
            //return false instead of assigning, but with this you can log all permission values
            if (!hasPermission(context, permission)) {
                hasAllPermissions = false;
                Log.e("hasPermissions","没有增加"+permission + "权限");
            }
        }

        return hasAllPermissions;

    }
}
