package com.base.okpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.base.utils.StatusBarUtil;
import com.capinfo.R;

import androidx.annotation.Nullable;

/**
 * Android 8.0 安装未知应用
 */

public class OKInstallApkPermissionActivity extends Activity {

    private static final String TAG = "OKPermissionActivity";

    private static OKPermissionListener sOKPermissionListener;

    private static final int INSTALL_APK_REQUEST_CODE = 1;

    private Context mContext;
    private Dialog mDialog = null;
    private Dialog mErrDialog = null;
    private boolean haveInstallPermission;

    public static void setOKPermissionListener(OKPermissionListener okPermissionListener) {
        sOKPermissionListener = okPermissionListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarUtil();

        mContext = this;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showApplyPermissionDialog();
        }else{
            finish();
        }
    }

    private void statusBarUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, android.R.color.white);
            StatusBarUtil.StatusBarLightMode(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void showApplyPermissionDialog() {
        haveInstallPermission = getPackageManager().canRequestPackageInstalls();
        if (haveInstallPermission) {
            if(null != sOKPermissionListener){
                sOKPermissionListener.onAppSettingsSuccess();
            }
            finish();
            return;
        }

       showOKPermissionErrorDialog(getString(R.string.okpermission_install_apk));
    }

    private void showOKPermissionErrorDialog(String permissions) {
        final OKPermissionErrorDialog dialog = new OKPermissionErrorDialog(this, R.style.okpermission_CustomDialog);
        dialog.setPermissionText(permissions);
        dialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != sOKPermissionListener) {
                    sOKPermissionListener.onRefusePermission();
                }
                dialog.dismiss();
                finish();
            }
        });
        dialog.setSettingsOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               gotoAppSettingsActivity();

            }
        });
        mErrDialog = dialog;
        dialog.show();
    }

    private void gotoAppSettingsActivity(){
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            startActivityForResult(intent, INSTALL_APK_REQUEST_CODE);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast.makeText(mContext,getString(R.string.okpermission_app_install_ok_apk_error),Toast.LENGTH_SHORT).show();
//            AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(getString(R.string.okpermission_app_install_ok_apk_error));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INSTALL_APK_REQUEST_CODE: {
                if (null != mErrDialog) {
                    mErrDialog.dismiss();
                    mErrDialog = null;
                }

                haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {
                    //还有未申请的
                    showOKPermissionErrorDialog(getString(R.string.okpermission_install_apk));
                } else {
                    //全部申请
                    if(null != sOKPermissionListener){
                        sOKPermissionListener.onAppSettingsSuccess();
                    }
                    finish();
                }

                break;
            }
        }
    }

    private void dismiss(Dialog dialog) {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismiss(mDialog);
        dismiss(mErrDialog);
        if (null != mDialog) {
            mDialog = null;
        }
        if (null != mErrDialog) {
            mErrDialog = null;
        }
        super.onDestroy();
    }
}
