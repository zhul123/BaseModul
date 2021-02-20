package com.component.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.base.utils.StatusBarUtil;
import com.base.widget.CustomProgressDialog;
import com.base.widget.DetachableDialogCancelListener;
import com.capinfo.R;
import com.component.providers.app.AppProvider;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends FragmentActivity implements IView, View.OnClickListener {
    public Unbinder unbinder;
    private Dialog dialog;
    private BaseApplication baseApplication;
    private TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getlayoutId());
        unbinder = ButterKnife.bind(this);

        ARouter.getInstance().inject(this);
//        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, R.color.white);
            StatusBarUtil.StatusBarLightMode(this);
        }

        initView();
        initData();
        initTitle();
    }

    /**
     * 销毁操作
     */
    @Override
    protected void onDestroy() {
        /*if (null != mPresenter) {
            mPresenter.onDestroy();
        }*/
        if (null != unbinder) {
            unbinder.unbind();
            unbinder = null;
        }
        EventBus.getDefault().unregister(this);
        CustomProgressDialog.cancle();

        super.onDestroy();
    }


    protected abstract int getlayoutId();


    public void initData() {
    }

    public void initView() {
        tv_title = findViewById(R.id.tv_title);
        View backView = findViewById(R.id.iv_back);
        if(backView != null){
            backView.setOnClickListener(this);
        }
    }

    public void initTitle() {
    }

    @Override
    public void onClick(View v) {
        if (isLive()){
            int id = v.getId();
            if (id == R.id.iv_back) {
                finish();
            } else if (id == R.id.iv_more) {
            }
        }
    }

    /**
     * 监听dialog的返回  取消后注销请求
     */
    DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            /*if (mPresenter != null) {
                mPresenter.unRegisterDispose();
            }*/
            finish();
        }
    };

    DetachableDialogCancelListener detachableDialogCancelListener = DetachableDialogCancelListener.wrap(onCancelListener);

    /**
     * loading统一处理
     */
    @Override
    public void showLoadingView() {
        if (!this.isFinishing() && !CustomProgressDialog.isShow()) {
            CustomProgressDialog.show(this, "", false, detachableDialogCancelListener);
        }
    }

    @Override
    public void hideLoadingView() {
        if (!this.isFinishing() && CustomProgressDialog.isShow()) {
            CustomProgressDialog.dimiss();
        }
    }

    protected void gotoNextByUri(Uri uri){
        ARouter.getInstance().navigation(AppProvider.class).gotoActivityByARouterUri(uri);
    }

    /**
     * 判断当前activity是否还活着
     *
     * @return
     */
    public boolean isLive() {
        if (isFinishing()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return false;
            }
        }
        return true;
    }
}
