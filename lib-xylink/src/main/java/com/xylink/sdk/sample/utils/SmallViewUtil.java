package com.xylink.sdk.sample.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ainemo.sdk.otf.VideoInfo;
import com.base.app.BaseApplication;
import com.base.utils.PageUtil;
import com.base.utils.ScreenUtils;
import com.xylink.sdk.sample.R;
import com.xylink.sdk.sample.activitys.xycall.XyCallActivity;
import com.xylink.sdk.sample.view.SpeakerVideoGroup;
import com.xylink.sdk.sample.view.VideoCell;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;
import static com.xylink.sdk.sample.share.SharingValues.REQUEST_FLOAT_PERMISSION;

/**
 * 视频小窗工具
 */
public class SmallViewUtil implements View.OnClickListener {
    static String TAG = "zlzlzlz";
    static SmallViewUtil instance;
    private boolean isAdd;
    private int screenWidth;
    private int halfX;
    private int offset = 60;

    protected VideoInfo localVideoInfo;

    public static SmallViewUtil getInstance() {
        if (instance == null) {
            synchronized (SmallViewUtil.class) {
                instance = new SmallViewUtil();
            }
        }
        return instance;
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View mView;
    private FrameLayout mBody;
    private Activity mActivity;
    private SpeakerVideoGroup mVideoGroup;

    public void init(Activity activity) {
        System.out.println("=========sminit");
        mActivity = activity;
        //sdk > 23 判断 是否有 android.permission.SYSTEM_ALERT_WINDOW 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(BaseApplication.getInstance())) {
            goToPermission(activity);
            return;
            //  23 > sdk >= 18  某些需要用AppOpsManager.checkOp方法检测判断
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if(!checkOp(activity,OP_SYSTEM_ALERT_WINDOW)){
                goToPermission(activity);
            }
        }
        if (windowManager == null) {
            // 获取WindowManager服务
            windowManager = (WindowManager) BaseApplication.getInstance().getSystemService(WINDOW_SERVICE);
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            // sdk > 26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                //24< sdk < 26 || sdk < 19
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                //19 <= sdk <= 24
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            float width = ScreenUtils.getScreenBounds(activity)[0];
            float height = ScreenUtils.getScreenBounds(activity)[1];

            //宽高自适应
            layoutParams.width = (int) (width / 6);
            layoutParams.height = (int) (height / 6);
            //显示的位置
            int[] bounds = ScreenUtils.getScreenBounds(BaseApplication.getInstance());
            screenWidth = bounds[0];
            halfX = screenWidth / 2;
            layoutParams.x = halfX - offset;
//                layoutParams.x = 0;
            layoutParams.y = bounds[1] / 2 - 150;
//                layoutParams.y = 0;
//                view = new FloatWindowView(mActivity);
        }

        mView = LayoutInflater.from(mActivity).inflate(R.layout.view_float_windows, null);
        mView.setOnTouchListener(new FloatingOnTouchListener());
        mVideoGroup = mView.findViewById(R.id.svg_video_group);
        mVideoGroup.setShowingPip(false);//关闭画中画
        mVideoGroup.setClickable(false);
        mVideoGroup.setFocusable(false);

    }

    /**
     * 对于某些厂商rom（小米），需要用AppOpsManager.checkOp方法检测判断，引导用户开启处理。
     * @param context
     * @param op
     * @return
     */
    private static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private boolean checkOp(Context context, int op) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    /**
     * 引导获取权限
     */
    private void goToPermission(Activity activity){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + XyCallActivity.class.getPackage().getName()));
        activity.startActivityForResult(intent, REQUEST_FLOAT_PERMISSION);
    }

    public void setLocalVideoInfo(VideoInfo localVideoInfo){
        this.localVideoInfo = localVideoInfo;
    }

    public void setvideoInfos(List<VideoInfo> videoInfos) {
        System.out.println("================setvideoInfos:"+videoInfos.size());
        if (mVideoGroup != null) {
            mVideoGroup.setRemoteVideoInfos(videoInfos);
        }
    }

    public void showFloatingWindow() {
        if(localVideoInfo != null){
            mVideoGroup.setLocalVideoInfo(localVideoInfo);
            mVideoGroup.setStopLocalTouch();
        }
        if(mVideoGroup != null) {
            int childCount = mVideoGroup.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View view = mVideoGroup.getChildAt(i);
                    if(view instanceof VideoCell){
                        VideoCell videoCell = (VideoCell) view;
                        videoCell.stopOntouch(true);
                    }
                }
            }
        }
        // 将悬浮窗控件添加到WindowManager
        if (!isAdd) {
            windowManager.addView(mView, layoutParams);
            isAdd = true;
        }
    }

    public void removeFloatView() {
        if (windowManager != null && mView != null && isAdd) {
            isAdd = false;
            windowManager.removeView(mView);
        }
    }

    private boolean isOpen = false;//是否已经打开列表

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x, downX;
        private int y, downY;

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
                    if (Math.abs(movedX) < 10 && Math.abs(movedY) < 10) {
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
                    if (Math.abs(upX - downX) < 20 && Math.abs(upY - downY) < 20) {
                        backClick();
                    }
                    if (upX > screenWidth / 2) {
                        layoutParams.x = halfX - offset;
                        // 更新悬浮窗控件布局
                    } else {
                        layoutParams.x = -halfX + offset;
                    }
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private void backClick() {
        if (PageUtil.getInstance().isDouble())
            return;
//        ActivityUtils.moveTaskToFront(mActivity);
        Intent intent = new Intent(mActivity, XyCallActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivity, 0, intent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
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


    public void stopAppByKill(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
//        mActivityManager.killBackgroundProcesses("com.capinfo.capmeeting");

        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
        int i = 1;
        for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
            System.out.println("========" + "PID: " + amProcess.pid + "(processName=" + amProcess.processName + "UID=" + amProcess.uid + ")");
        }
    }

    public void destory() {
//        NemoSDK.getInstance().hangup();
//        NemoSDK.getInstance().releaseLayout();
//        NemoSDK.getInstance().releaseCamera();
        if (mVideoGroup != null) {
            mVideoGroup.destroy();
            mVideoGroup = null;
        }
        windowManager = null;
        mView = null;
    }
}
