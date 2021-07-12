package com.base.okpermission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;

import static com.base.okpermission.OKPermissionActivity.INTENT_KEY_DIALOG_ITEMS;
import static com.base.okpermission.OKPermissionActivity.INTENT_KEY_DIALOG_MSG;
import static com.base.okpermission.OKPermissionActivity.INTENT_KEY_DIALOG_TITLE;
import static com.base.okpermission.OKPermissionActivity.INTENT_KEY_SHOW_DIALOG;
import static com.base.okpermission.OKPermissionActivity.INTENT_KEY_SHOW_ERROR_DIALOG;

/**
 * @author :  jiahongfei
 * @email : jiahongfeinew@163.com
 * @date : 2018/1/18
 * @desc : OKPermission的管理类，用于申请权限、配置各种参数
 */

public class OKPermissionManager {

    private Bundle mBundle = new Bundle();
    private OKPermissionListener mOKPermissionListener;
    private OKPermissionKeyBackListener mKeyBackListener;

    private OKPermissionManager(Builder builder) {

        mBundle.putBoolean(INTENT_KEY_SHOW_DIALOG, builder.mShowDialog);

        mBundle.putString(INTENT_KEY_DIALOG_TITLE, builder.mDialogTitle);
        mBundle.putString(INTENT_KEY_DIALOG_MSG, builder.mDialogMsg);
        mBundle.putSerializable(INTENT_KEY_DIALOG_ITEMS, builder.mDialogItems);
        mBundle.putBoolean(INTENT_KEY_SHOW_ERROR_DIALOG,builder.mShowErrorDialog);

        mOKPermissionListener = builder.mOKPermissionListener;
        mKeyBackListener = builder.mKeyBackListener;
    }

    /**
     * 应用权限
     *
     * @param context
     */
    public void applyPermission(final Context context) {
        try {
            OKPermissionActivity.setOKPermissionListener(mOKPermissionListener);
            OKPermissionActivity.setKeyBackListener(mKeyBackListener);
            Intent intent = new Intent(context, OKPermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(mBundle);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private String mDialogTitle;
        private String mDialogMsg;
        private ArrayList<PermissionItem> mDialogItems;
        private boolean mShowDialog = false;
        private boolean mShowErrorDialog = true;
        private OKPermissionListener mOKPermissionListener;
        private OKPermissionKeyBackListener mKeyBackListener;

        public Builder(PermissionItem[] permissions) {
            mDialogItems = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                mDialogItems.add(permissions[i]);
            }
        }

        public Builder setShowDialog(boolean showDialog) {
            mShowDialog = showDialog;
            return this;
        }

        public Builder setShowErrorDialoig(boolean showErrorDialog){
            mShowErrorDialog = showErrorDialog;
            return this;
        }

        public Builder setDialogTitle(String dialogTitle) {
            mDialogTitle = dialogTitle;
            return this;
        }

        public Builder setDialogMsg(String dialogMsg) {
            mDialogMsg = dialogMsg;
            return this;
        }

        public Builder setOKPermissionListener(OKPermissionListener permissionListener) {
            mOKPermissionListener = permissionListener;
            return this;
        }

        public Builder setKeyBackListener(OKPermissionKeyBackListener keyBackListener) {
            mKeyBackListener = keyBackListener;
            return this;
        }

        public OKPermissionManager builder() {
            return new OKPermissionManager(this);
        }

    }

    /**
     * 申请权限快捷方法，申请权限不弹出对话框
     *
     * @param context
     * @param permissionItems
     * @param permissionListener
     * @return true 都申请成功，false有未申请成功
     */
    public static boolean applyPermissionNoDialog(Context context, PermissionItem[] permissionItems, OKPermissionListener permissionListener) {
        return applyPermissionNoDialog(context,true,permissionItems,permissionListener);
    }

    /**
     * 申请权限快捷方法，申请权限不弹出对话框
     *
     * @param context
     * @param permissionItems
     * @param showErrorDialog
     * @param permissionListener
     * @return true 都申请成功，false有未申请成功
     */
    public static boolean applyPermissionNoDialog(Context context,boolean showErrorDialog ,PermissionItem[] permissionItems, OKPermissionListener permissionListener) {
        OKPermissionManager okPermissionManager = new OKPermissionManager
                .Builder(permissionItems)
                .setOKPermissionListener(permissionListener)
                .setShowDialog(false)
                .setShowErrorDialoig(showErrorDialog)
                .builder();
        if (OKPermission.checkPermission(context, permissionItems)) {
            return true;
        }
        okPermissionManager.applyPermission(context);
        return false;
    }


    /**
     * 核对权限是否都申请过了
     *
     * @param context
     * @param permissionItems
     * @return
     */
    public static boolean checkPermissions(Context context, PermissionItem[] permissionItems) {
        return OKPermission.checkPermission(context, permissionItems);
    }

    /**
     * 核对权限是否都选择"禁止后不再询问"
     * @param activity
     * @param permissionItems
     * @return  true:都选择"禁止后不再询问" ； false
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, PermissionItem[] permissionItems){
        boolean result = true;
        if(null == activity || null == permissionItems){
            return result;
        }
        for (PermissionItem item:permissionItems) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, item.permission)){
                //没有勾选"进之后不再询问"
                result = false;
            }
        }
        return result;
    }

    /**
     * 用于获取 未知来源应用 权限
     *
     * @param context
     */
    public static void applyInstallApkPermission(Context context, OKPermissionListener okPermissionListener) {
        OKInstallApkPermissionActivity.setOKPermissionListener(okPermissionListener);
        Intent intent = new Intent(context, OKInstallApkPermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
