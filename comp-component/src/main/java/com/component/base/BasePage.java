package com.component.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.base.http.MyHttpUtils;
import com.base.widget.CustomProgressDialog;
import com.base.widget.DetachableDialogCancelListener;
import com.component.BuildConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public abstract class BasePage<T extends IPresenter> extends LinearLayout implements LifecycleObserver, IView {
    private String tag = BasePage.class.getSimpleName();

    protected T mPresenter;

    public BasePage(Context context) {
        super(context);
        init();
    }

    public BasePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasePage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasePage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        EventBus.getDefault().register(this);
        ((LifecycleOwner) getContext()).getLifecycle().addObserver(this);
        mPresenter = (T) createPresenter();
    }

    //基类用泛型，此处其实可以用T，mPresenter赋值时无需强转，
    protected abstract IPresenter createPresenter();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public abstract void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        if(mPresenter != null) {
            mPresenter.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){

    };

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){

    };

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){

    };

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){

    };
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny(){

    };

    /**
     * 监听dialog的返回  取消后注销请求
     */
    DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            /*if (mPresenter != null) {
                mPresenter.unRegisterDispose();
            }*/
            MyHttpUtils.destroy();
        }
    };

    DetachableDialogCancelListener detachableDialogCancelListener = DetachableDialogCancelListener.wrap(onCancelListener);
    /**
     * loading统一处理
     */
    @Override
    public void showLoadingView() {
        if (!CustomProgressDialog.isShow()) {
            CustomProgressDialog.show(getContext(), "", false, detachableDialogCancelListener);
        }
    }

    @Override
    public void hideLoadingView() {
        if (CustomProgressDialog.isShow()) {
            CustomProgressDialog.dimiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(Object o){

    }

}
