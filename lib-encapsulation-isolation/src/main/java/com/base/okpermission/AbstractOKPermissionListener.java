package com.base.okpermission;


import androidx.annotation.NonNull;

/**
 * @author :  jiahongfei
 * @email : jiahongfeinew@163.com
 * @date : 2018/8/10
 * @desc :
 */

public abstract class AbstractOKPermissionListener implements OKPermissionListener {
    /**
     * 系统对话框全部点击"始终允许"回调
     * @param permissions
     * @param grantResults
     * @param success
     */
    @Override
    public void onOKPermission(@NonNull String[] permissions, @NonNull int[] grantResults, boolean success) {

    }
    /**
     * 拒绝设置权限
     */
    @Override
    public void onRefusePermission() {
    }
    /**
     * 设置页面全部申请权限返回
     */
    @Override
    public void onAppSettingsSuccess() {
    }
}
