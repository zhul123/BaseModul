package com.base.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.base.app.BaseApplication;

import java.lang.reflect.Method;

/**
 * @author zhulei
 * @Description: 和屏幕相关的(屏幕宽高等 ）
 * @date 2021/01/01
 */
public class ScreenUtils {


    static float sNoncompatDensity = 0;
    static float sNoncompatScaledDensity = 0;

    /**
     * 屏幕适配，按宽度适配，360表示设计图宽度360dp
     * @param activity
     */
    public static void setCustomDensityForWidth(Activity activity) {
        setCustomDensityForWidth(BaseApplication.getInstance(),activity,1280);
    }


    /**
     * 屏幕适配
     */
    public static void setCustomDensityForWidth(Application application, Activity activity, float widthDp) {


        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if(sNoncompatDensity == 0){
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if(newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        float targetForWidthDensity = appDisplayMetrics.widthPixels / widthDp;//360dp为设计图宽度
        float targetScaledDensity = targetForWidthDensity * (sNoncompatScaledDensity/sNoncompatDensity);
        int targetForWidthDensityDpi = (int) (160 * targetForWidthDensity);

        appDisplayMetrics.density = appDisplayMetrics.scaledDensity = targetForWidthDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetForWidthDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = activityDisplayMetrics.scaledDensity = targetForWidthDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetForWidthDensityDpi;
    }


    /**
     * 屏幕适配，按宽度适配，360表示设计图宽度360dp
     * @param activity
     */
    public static void setCustomDensityForHeight(Activity activity) {
        setCustomDensityForHeight(BaseApplication.getInstance(),activity,900);
    }
    /**
     * 屏幕适配
     */
    public static void setCustomDensityForHeight(Application application, Activity activity, float heightDp) {


        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if(sNoncompatDensity == 0){
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if(newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        float targetForHeightDensity = appDisplayMetrics.heightPixels / heightDp;//360dp为设计图宽度
        float targetScaledDensity = targetForHeightDensity * (sNoncompatScaledDensity/sNoncompatDensity);
        int targetForHeightDensityDpi = (int) (160 * targetForHeightDensity);

        appDisplayMetrics.density = appDisplayMetrics.scaledDensity = targetForHeightDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetForHeightDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = activityDisplayMetrics.scaledDensity = targetForHeightDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetForHeightDensityDpi;
    }



    /**
     * 手动计算Density，基准屏幕360，
     * 例如：乐视1080的手机density为2.65，但是标准的是3
     *
     * @param screenWidth
     * @return
     */
    public static float getDensityBase360px(int screenWidth) {
        final int BASE_SCREEN = 360;
        return screenWidth / BASE_SCREEN;
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return 数组第一个元素宽，第二个元素高
     */
    public static int[] getScreenBounds(Context context) {

        int[] screenBounds = new int[2];

        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        screenBounds[0] = displayMetrics.widthPixels;
        screenBounds[1] = displayMetrics.heightPixels;
        return screenBounds;
    }

    /**
     * 根据dip返回当前设备上的px值
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, int dip) {
        int px = 0;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float density = dm.density;
        px = (int) (dip * density);
        return px;
    }
    /**
     * 根据dip返回当前设备上的px值
     *
     * @param dip
     * @return
     */
    public static int dipToPx(int dip) {
        int px = 0;
        DisplayMetrics dm = new DisplayMetrics();
        dm = BaseApplication.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        float density = dm.density;
        px = (int) (dip * density);
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int sp2px(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    public static int getTouchSlop(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        return configuration.getScaledTouchSlop();
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    /**
     * 这个方法是将view的左上角坐标存入数组中.此坐标是相对当前activity而言.
     * 若是普通activity,则y坐标为可见的状态栏高度+可见的标题栏高度+view左上角到标题栏底部的距离.
     * 可见的意思是:在隐藏了状态栏/标题栏的情况下,它们的高度以0计算.
     * 若是对话框式的activity,则y坐标为可见的标题栏高度+view到标题栏底部的距离. 此时是无视状态栏的有无的.
     *
     * @param view
     * @return
     */
    public static int[] getLocationInWindow(View view) {
        int[] position = new int[2];
        view.getLocationInWindow(position);
        System.out.println("getLocationInWindow:" + position[0] + ","
                + position[1]);
        return position;
    }

    /**
     * 这个方法跟上面的差不多,也是将view的左上角坐标存入数组中.但此坐标是相对整个屏幕而言. y坐标为view左上角到屏幕顶部的距离.
     *
     * @param view
     * @return
     */
    public static int[] getLocationOnScreen(View view) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        System.out.println("getLocationOnScreen:" + position[0] + ","
                + position[1]);
        return position;
    }

    /**
     * 这个方法是构建一个Rect用来"套"这个view.此Rect的坐标是相对当前activity而言.
     * 若是普通activity,则Rect的top为可见的状态栏高度+可见的标题栏高度+Rect左上角到标题栏底部的距离.
     * 若是对话框式的activity,则y坐标为Rect的top为可见的标题栏高度+Rect左上角到标题栏底部的距离. 此时是无视状态栏的有无的.
     *
     * @param view
     * @return
     */
    public static Rect getGlobalVisibleRect(View view) {
        Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);
        System.out.println(viewRect);
        return viewRect;
    }

    /**
     * 这个方法获得的Rect的top和left都是0,也就是说,仅仅能通过这个Rect得到View的宽度和高度....
     *
     * @param view
     * @return
     */
    public static Rect getLocalVisibleRect(View view) {
        Rect globeRect = new Rect();
        view.getLocalVisibleRect(globeRect);
        return globeRect;
    }

    /**
     * 获得状态栏高度
     *
     * @param mContext
     * @return
     */
    public static int getStatusBarHeight(Context mContext) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getRealHeightPixels(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = 0;
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {

        }
        return height;
    }

    /**
     * 获取导航栏高度, 可能未显示
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 获取是否存在NavigationBar
     * 无法判断支持动态设置的ROM
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    /**
     * 获取虚拟导航栏(NavigationBar)是否显示
     * @return true 表示虚拟导航栏显示，false 表示虚拟导航栏未显示
     */
    public static boolean hasNavigationBar(Context context) {
        if (getNavigationBarHeight(context) == 0) return false;
        if (RomUtil.isEmui() && isHuaWeiHideNav(context)) return false;
        if (RomUtil.isMiui() && isMiuiFullScreen(context)) return false;
        if (RomUtil.isVivo() && isVivoFullScreen(context)) return false;
        return isHasNavigationBar(context);
    }

    /**
     * 判断华为手机是否隐藏虚拟导航栏
     * @param context
     * @return true 使用手势  false 使用虚拟导航栏
     */
    private static boolean isHuaWeiHideNav(Context context) {
        int flag;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            flag = Settings.System.getInt(context.getContentResolver(), "navigationbar_is_min", 0);
        } else {
            flag = Settings.Global.getInt(context.getContentResolver(), "navigationbar_is_min", 0);
        }
        return flag != 0;
    }

    /**
     * 小米手机是否开启手势操作
     * @param context
     * @return true 使用手势 false 使用虚拟导航栏（默认行为）
     */
    private static boolean isMiuiFullScreen(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return (Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0)) != 0;
        }
        return false;
    }

    /**
     * Vivo手机是否开启手势操作
     * @param context
     * @return true 使用手势  false 使用虚拟导航栏（默认行为）
     */
    private static boolean isVivoFullScreen(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0) != 0;
    }

    /**
     * 根据屏幕真实高度与显示高度，判断虚拟导航栏是否显示
     * @return true 表示虚拟导航栏显示，false 表示虚拟导航栏未显示
     */
    private static boolean isHasNavigationBar(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        // 部分无良厂商的手势操作，显示高度 + 导航栏高度，竟然大于物理高度，对于这种情况，直接默认未启用导航栏
        if (displayHeight > displayWidth) {
            if (displayHeight + getNavigationBarHeight(context) > realHeight) return false;
        } else {
            if (displayWidth + getNavigationBarHeight(context) > realWidth) return false;
        }

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /*
     * 注意:以上方法在OnCreate方法中调用,都会返回0,这是因为View还未加载完毕.建议在onWindowFocusChanged方法中进行获取,
     * 有些情况下onWindowFocusChanged不好用的时候(比如ActivityGroup),可以这样写:
     * mTextView.post(new Runnable() {
     *
     * @Override public void run() { Rect viewRect = new Rect();
     * mTextView.getGlobalVisibleRect(viewRect);
     * mTreeScrollView.setRect(viewRect); } });
     */
    // ///////////////////////////////////////////////////////////////////////////////////

    public static boolean isKeyboardShowing(Activity context) {
        long time = System.currentTimeMillis();
        int screenHeight = context.getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight * 2 / 3 > rect.bottom;

    }
}
