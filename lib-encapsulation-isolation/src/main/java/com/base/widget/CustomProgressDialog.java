package com.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.capinfo.R;

/**
 * Created by zl
 * 自定义进度圈圈
 * 不在限制跟布局必须要用relativeLayout
 */
public class CustomProgressDialog extends Dialog {
    private static CustomProgressDialog dialog;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context        上下文
     * @param message        提示
     * @param cancelable     是否可取消
     * @param cancelListener 按下返回键监听
     * @return
     */
    public static CustomProgressDialog show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        dimiss();
        dialog = new CustomProgressDialog(context, R.style.Custom_Progress);
        dialog.setTitle("");
        dialog.setContentView(R.layout.layout_progress_dialog);

        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.tv_message).setVisibility(View.GONE);
        } else {
            TextView tv_message = (TextView) dialog.findViewById(R.id.tv_message);
            tv_message.setText(message);
        }
        // 按返回键是否取消
        dialog.setCanceledOnTouchOutside(cancelable);
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.1f;
        dialog.getWindow().setAttributes(lp);
        if (cancelListener instanceof DetachableDialogCancelListener) {
            ((DetachableDialogCancelListener) cancelListener).clearOnDetach(dialog);
        }
        dialog.show();

        return dialog;
    }

    /**
     * 关闭dialog
     */
    public static void dimiss() {
        try {
            if (isShow()) {
                dialog.dismiss();
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * 关闭dialog
     */
    public static void cancle() {
        try {
            if (isShow()) {
                dialog.dismiss();
            }
        } catch (Exception ignored) {

        } finally {
            dialog = null;
        }
    }

    /**
     * dialog状态
     */
    public static boolean isShow() {
        return dialog != null && dialog.isShowing();
    }


    @Override
    public void onDetachedFromWindow() {
        cancle();
        super.onDetachedFromWindow();
    }
}

