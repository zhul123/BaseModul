package com.base.okpermission;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


import com.capinfo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author :  jiahongfei
 * @email : jiahongfeinew@163.com
 * @date : 2018/1/18
 * @desc :
 */

public class OKPermissionErrorDialog extends Dialog {

    public interface DialogKeyBackListener{
        void onKeyBackListener();
    }

    private Context mContext;
    private View.OnClickListener mCancelOnClickListener;
    private View.OnClickListener mSettingsOnClickListener;
    private DialogKeyBackListener mDialogKeyBackListener;

    public OKPermissionErrorDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public OKPermissionErrorDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected OKPermissionErrorDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setContentView(R.layout.okpermission_dialog_okpermission_error);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCancelOnClickListener) {
                    mCancelOnClickListener.onClick(v);
                }
            }
        });
        findViewById(R.id.settinsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSettingsOnClickListener) {
                    mSettingsOnClickListener.onClick(v);
                }
            }
        });
    }

    public void setPermissionText(String permission){
        TextView msgTextView = (TextView)findViewById(R.id.msgTextView);
        msgTextView.setText(mContext.getString(R.string.okpermission_show_permission_dialog_msg,permission));
        TextView titleTextView = (TextView)findViewById(R.id.titleTextView);
        titleTextView.setText(mContext.getString(R.string.okpermission_dialog_error_title,permission));
    }

    public void setPermissionTitle(String title){
        TextView titleTextView = (TextView)findViewById(R.id.titleTextView);
        titleTextView.setText(mContext.getString(R.string.okpermission_dialog_error_title,title));
    }

    public void setPermissionMsg(String msg){
        TextView msgTextView = (TextView)findViewById(R.id.msgTextView);
        msgTextView.setText(msg);
    }

    public void setCancelOnClickListener(View.OnClickListener cancelOnClickListener) {
        mCancelOnClickListener = cancelOnClickListener;
    }

    public void setSettingsOnClickListener(View.OnClickListener settingsOnClickListener) {
        mSettingsOnClickListener = settingsOnClickListener;
    }

    public void setDialogKeyBackListener(DialogKeyBackListener dialogKeyBackListener) {
        mDialogKeyBackListener = dialogKeyBackListener;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
//            if(null != mDialogKeyBackListener){
//                mDialogKeyBackListener.onKeyBackListener();
//            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
