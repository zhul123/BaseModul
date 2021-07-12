package com.base.okpermission;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.base.utils.StatusBarUtil;
import com.capinfo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 */

public class OKPermissionActivity extends Activity {

    private static final String TAG = "OKPermissionActivity";

    private static OKPermissionListener sOKPermissionListener;
    private static OKPermissionKeyBackListener sKeyBackListener;

    private static final int APP_SETTINGS_REQUEST_CODE = 0;

    /**
     * 是否弹出提示对话框
     */
    public static final String INTENT_KEY_SHOW_DIALOG = "intent_key_show_dialog";

    /**
     * 提示框标题
     */
    public static final String INTENT_KEY_DIALOG_TITLE = "intent_key_dialog_title";
    /**
     * 提示框提示语
     */
    public static final String INTENT_KEY_DIALOG_MSG = "intent_key_dialog_msg";
    /**
     * 对话框Item
     */
    public static final String INTENT_KEY_DIALOG_ITEMS = "intent_key_dialog_items";

    /**
     * 是否弹出全部禁止权限后的对话框
     */
    public static final String INTENT_KEY_SHOW_ERROR_DIALOG = "intent_key_show_error_dialog";

    private Context mContext;
    //    private String[] mPermissions;
    private String mDialogTitle;
    private String mDialogMsg;
    private ArrayList<PermissionItem> mDialogItems = new ArrayList<>();
    private boolean mShowDialog;
    private boolean mShowErrorDialog = true;
    private Dialog mDialog = null;
    private Dialog mErrDialog = null;

    public static void setOKPermissionListener(OKPermissionListener okPermissionListener) {
        sOKPermissionListener = okPermissionListener;
    }

    public static void setKeyBackListener(OKPermissionKeyBackListener keyBackListener) {
        sKeyBackListener = keyBackListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        statusBarUtil();

        mContext = this;

        getIntentData(savedInstanceState);

        showApplyPermissionDialog();
    }

    private void statusBarUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, android.R.color.transparent);
            StatusBarUtil.StatusBarLightMode(this);
        }
    }
    private void getIntentData(Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if(null == bundle){
            bundle = getIntent().getExtras();
        }
        if(null == bundle){
            return ;
        }
        mDialogTitle = bundle.getString(INTENT_KEY_DIALOG_TITLE, null);
        mDialogMsg = bundle.getString(INTENT_KEY_DIALOG_MSG, null);
        mDialogItems = (ArrayList<PermissionItem>) bundle.getSerializable(INTENT_KEY_DIALOG_ITEMS);
        mShowDialog = bundle.getBoolean(INTENT_KEY_SHOW_DIALOG, false);
        mShowErrorDialog = bundle.getBoolean(INTENT_KEY_SHOW_ERROR_DIALOG,true);
    }

    private void showApplyPermissionDialog() {
        final List<String> requestPermission = OKPermission.requestPermission(mContext, mDialogItems.toArray(new PermissionItem[mDialogItems.size()]));
        if (requestPermission.size() <= 0) {
            finish();
            return;
        }
        if (mShowDialog) {
            showOKPermissionDialog(requestPermission);
        } else {
            OKPermission.okPermission((Activity) mContext, requestPermission);
        }
    }

    private void showOKPermissionDialog(final List<String> requestPermission) {
        final OKPermissionDialog okPermissionDialog = new OKPermissionDialog(mContext, R.style.okpermission_CustomDialog);
        okPermissionDialog.setOKPermissionTitle(mDialogTitle);
        okPermissionDialog.setOKPermissionMessage(mDialogMsg);
        okPermissionDialog.setRecyclerView(requestPermission, mDialogItems);
        okPermissionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okPermissionDialog.dismiss();
                OKPermission.okPermission((Activity) mContext, requestPermission);

            }
        });
        okPermissionDialog.setDialogKeyBackListener(new OKPermissionDialog.DialogKeyBackListener() {
            @Override
            public void onKeyBackListener() {
                if (null != sKeyBackListener) {
                    sKeyBackListener.onKeyBackListener();
                }
                finish();
            }
        });
        mDialog = okPermissionDialog;
        okPermissionDialog.show();
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
                gotoAppSettingsActivity(OKPermissionActivity.this);

            }
        });
        mErrDialog = dialog;
        dialog.show();
    }

    public void gotoAppSettingsActivity(Activity context) {
        try {
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
            context.startActivityForResult(intent, APP_SETTINGS_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext,context.getString(R.string.okpermission_app_settings_error),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case OKPermission.REQUEST_CODE_PERMISSION: {
                if (null != sOKPermissionListener) {
                    List<String> tmpPermissions = OKPermission.requestPermission(this, mDialogItems.toArray(new PermissionItem[mDialogItems.size()]));
                    sOKPermissionListener.onOKPermission(permissions, grantResults, tmpPermissions.size() <= 0);
//                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,tmpPermissions.get(0))){
//                    }else {
                    if (tmpPermissions.size() > 0  && mShowErrorDialog) {
                        String msg = getOKPermissionErrorDialogMsg(mContext,mDialogItems,tmpPermissions);
                        showOKPermissionErrorDialog(msg);
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
//                    }

                break;
            }
            default:
                break;
        }
    }

    public static String getOKPermissionErrorDialogMsg(Context context,List<PermissionItem> mDialogItems,List<String> tmpPermissions) {

        if (null != mDialogItems && mDialogItems.size() > 0 && null != tmpPermissions && tmpPermissions.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<Integer> stringSet = new TreeSet<>();
            for (String permission : tmpPermissions) {
                int index = mDialogItems.indexOf(new PermissionItem(permission, 0, 0));
                if (-1 != index) {
                    if (stringSet.add(mDialogItems.get(index).nameId)) {
                        stringBuilder.append(context.getString(mDialogItems.get(index).nameId)).append(",");
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            }
            return stringBuilder.toString();

        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_SETTINGS_REQUEST_CODE: {
                if (null != mErrDialog) {
                    mErrDialog.dismiss();
                    mErrDialog = null;
                }

                List<String> tmpPermissions = OKPermission.requestPermission(this, mDialogItems.toArray(new PermissionItem[mDialogItems.size()]));
                if (tmpPermissions.size() > 0) {
                    //还有未申请的
                    String msg = getOKPermissionErrorDialogMsg(mContext,mDialogItems,tmpPermissions);
                    showOKPermissionErrorDialog(msg);

                } else {
                    //全部申请
                    if (null != sOKPermissionListener) {
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            mDialogTitle = savedInstanceState.getString(INTENT_KEY_DIALOG_TITLE, null);
            mDialogMsg = savedInstanceState.getString(INTENT_KEY_DIALOG_MSG, null);
            mDialogItems = (ArrayList<PermissionItem>) savedInstanceState.getSerializable(INTENT_KEY_DIALOG_ITEMS);
            mShowDialog = savedInstanceState.getBoolean(INTENT_KEY_SHOW_DIALOG, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (null != outState) {
            outState.putString(INTENT_KEY_DIALOG_TITLE, mDialogTitle);
            outState.putString(INTENT_KEY_DIALOG_MSG, mDialogMsg);
            outState.putSerializable(INTENT_KEY_DIALOG_ITEMS, mDialogItems);
            outState.putBoolean(INTENT_KEY_SHOW_DIALOG, mShowDialog);
        }

        super.onSaveInstanceState(outState);

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
        sOKPermissionListener = null;
        sKeyBackListener = null;
        super.onDestroy();
    }
}
