package com.base.okpermission;


import androidx.annotation.NonNull;

/**
 * @author :  jiahongfei
 * @email : jiahongfeinew@163.com
 * @date : 2018/1/28
 * @desc : 权限申请结束的监听，用于判断权限是否申请成功或者失败
 */

public interface OKPermissionListener {
    /**
     * 权限操作结束回调
     * @param permissions
     * @param grantResults
     * @param success
     */
    void onOKPermission(@NonNull String[] permissions, @NonNull int[] grantResults, boolean success);

    /**
     * 拒绝设置权限
     */
    void onRefusePermission();

    void onAppSettingsSuccess();
}
