package com.xylink.sdk.sample.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.base.app.BaseApplication;
import com.base.utils.ScreenUtils;
import com.xylink.sdk.sample.activitys.xycall.GalleryVideoFragment;
import com.xylink.sdk.sample.R;

import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class FloatViewUtil implements View.OnClickListener {
    static FloatViewUtil instance;
    private boolean isAdd;
    private int screenWidth;
    private int halfX;
    private int offset = 60;
    private GalleryVideoFragment mGalleryVideoFragment;

    public static FloatViewUtil getInstance() {
        if (instance == null) {
            synchronized (FloatViewUtil.class) {
                instance = new FloatViewUtil();
            }
        }
        return instance;
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View view;
    private FrameLayout mBody;

    public void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(BaseApplication.getInstance())) {
            if (windowManager == null) {
                // 获取WindowManager服务
                windowManager = (WindowManager) BaseApplication.getInstance().getSystemService(WINDOW_SERVICE);
                // 设置LayoutParam
                layoutParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //宽高自适应
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                //显示的位置
                int [] bounds = ScreenUtils.getScreenBounds(BaseApplication.getInstance());
                screenWidth = bounds[0];
                halfX  = screenWidth /2;
                layoutParams.x = halfX - offset;
                layoutParams.y = bounds[1]/2 - 150;

                // 新建悬浮窗控件
                view = LayoutInflater.from(BaseApplication.getInstance()).inflate(R.layout.layout_float, null);
//                mBody = view.findViewById(R.id.ll_float_main);
////                mBtn.setOnClickListener(this);
//                view.setOnTouchListener(new FloatingOnTouchListener());
//                mGalleryVideoFragment = new GalleryVideoFragment();
//                view.getContext().getgetSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
//                mBody..(mGalleryVideoFragment);
            }
            isAdd = true;
            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(view, layoutParams);
        }
    }

    public void removeFloatView() {
        if (windowManager != null && view != null && isAdd) {
            isAdd = false;
            windowManager.removeView(view);
        }
    }

    private boolean isOpen = false;//是否已经打开列表
    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x,downX;
        private int y,downY;
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    downX = x;
                    downY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    //防抖动
                    if(Math.abs(movedX) < 10 && Math.abs(movedY) < 10){
                        return false;
                    }
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    int upX = (int) event.getRawX();
                    int upY = (int) event.getRawY();
                    if(Math.abs(upX - downX) < 20 && Math.abs(upY - downY) < 20){
                        btnClick();
                    }
                    if(upX > screenWidth/2){
                        layoutParams.x = halfX - offset;
                        // 更新悬浮窗控件布局
                    }else{
                        layoutParams.x = - halfX + offset;
                    }
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private ImageView mBackView;
    private ImageView mBoardView;
    private int touchTime = 0;
    private void btnClick(){
        if(view != null){
            isOpen = touchTime %2 == 0;
            mBackView.setOnClickListener(this);
            mBoardView.setOnClickListener(this);
            mBackView.setVisibility(touchTime %2 == 0 ? View.VISIBLE : View.INVISIBLE);
            mBoardView.setVisibility(touchTime %2 == 0 ? View.VISIBLE : View.INVISIBLE);
            touchTime ++;
        }
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
            case R.id.img_back:
                ActivityTaskUtil.getInstance().backToTop();
                break;
            case R.id.img_note:
                SystemAppUtils.gotoNoteBoardApp();
                break;
            case R.id.img_btn:
                btnClick();
                break;
        }*/
    }


    public void stopAppByKill(Context context , String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
//        mActivityManager.killBackgroundProcesses("com.capinfo.capmeeting");

        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
        int i = 1;
        for (ActivityManager.RunningAppProcessInfo amProcess :mRunningProcess){
            System.out.println("========"+"PID: " +amProcess.pid + "(processName=" + amProcess.processName + "UID="+amProcess.uid+")");
        }
    }
}
