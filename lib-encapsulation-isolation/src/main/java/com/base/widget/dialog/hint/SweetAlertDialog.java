package com.base.widget.dialog.hint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capinfo.R;


public class SweetAlertDialog extends Dialog implements View.OnClickListener {

    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
//    private SuccessTickView mSuccessTick;
    private Drawable mCustomImgDrawable;
    private ImageView mCustomImage;
    private TextView mConfirmButton;
    private TextView mCancelButton;
    private AlertCallBack callBack;
    private boolean isLeft;

    public SweetAlertDialog(Context context){
        this(context,false);
    }

    public SweetAlertDialog(Context context, boolean canBack) {
        super(context, R.style.alert_hint_dialog);
        setCancelable(canBack);
        setCanceledOnTouchOutside(canBack);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_hint_alert);

        mTitleTextView = (TextView)findViewById(R.id.title_text);
        mContentTextView = (TextView)findViewById(R.id.content_text);
        mConfirmButton = (TextView)findViewById(R.id.confirm_button);
        mCancelButton = (TextView)findViewById(R.id.cancel_button);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);

    }

    public AlertCallBack getCallBack() {
        return callBack;
    }

    public SweetAlertDialog setCallBack(AlertCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public String getTitleText () {
        return mTitleText;
    }

    public SweetAlertDialog setTitleText (String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }
    public SweetAlertDialog setTitleText (String text, boolean isLeft) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage (Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage (int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText () {
        return mContentText;
    }

    public SweetAlertDialog setContentText (String text, Html.TagHandler tagHandler) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
                mContentTextView.setText(Html.fromHtml(mContentText,null,tagHandler));
        }
        return this;
    }
    public SweetAlertDialog setContentText (String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(Html.fromHtml(mContentText));
            if(isLeft){
                mContentTextView.setGravity(Gravity.LEFT);
            }
        }
        return this;
    }
    public SweetAlertDialog setContentText (String text, boolean isLeft) {
        this.isLeft = isLeft;
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentTextView.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            mContentTextView.setLayoutParams(layoutParams);
            layoutParams = null;
            mContentTextView.setGravity(Gravity.LEFT);

            mContentTextView.setText(Html.fromHtml(mContentText));
            showContentText(true);
        }
        return this;
    }

    public SweetAlertDialog setContentText (Spannable sp) {
        if (mContentTextView != null && sp != null) {
            showContentText(true);
            mContentTextView.setText(sp);
        }
        return this;
    }

    public SweetAlertDialog showCancelButton (boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }


    public SweetAlertDialog showContentText (boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public SweetAlertDialog setCancelText (String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public SweetAlertDialog setConfirmText (String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if(callBack != null){
                callBack.onCancelClick(this);
            }
        } else if (v.getId() == R.id.confirm_button) {
            if(callBack != null){
                callBack.oncinfirmClick(this);
            }
        }
    }


   public interface AlertCallBack{
        void onCancelClick(SweetAlertDialog dialog);
        void oncinfirmClick(SweetAlertDialog dialog);
    }
}