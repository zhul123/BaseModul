package com.base.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.ViewTreeObserver;

/**
 * @author wangshaobo
 * @email w317338175@163.com
 * @date 2020/4/24
 * @desc 解决BaseActivity的dialog的OnCancelListener内存泄漏
 */
public final class DetachableDialogCancelListener implements DialogInterface.OnCancelListener {
    public static DetachableDialogCancelListener wrap(DialogInterface.OnCancelListener delegate) {
        return new DetachableDialogCancelListener(delegate);
    }

    private DialogInterface.OnCancelListener delegateOrNull;

    private DetachableDialogCancelListener(DialogInterface.OnCancelListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onCancel(dialog);
        }
    }

    public void clearOnDetach(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Context context = dialog.getContext();
            if (!(context instanceof Activity)) {
                if (context instanceof ContextThemeWrapper) {
                    context = ((ContextThemeWrapper) context).getBaseContext();
                }
            }
            if (context instanceof Activity) {
                ((Activity) context).getWindow()
                        .getDecorView()
                        .getViewTreeObserver()
                        .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                            @Override
                            public void onWindowAttached() {
                            }

                            @Override
                            public void onWindowDetached() {
                                delegateOrNull = null;
                            }
                        });
            }
        }
    }
}
