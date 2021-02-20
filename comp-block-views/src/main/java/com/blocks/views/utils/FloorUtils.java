package com.blocks.views.utils;

public class FloorUtils {
    private static long lastClickTime;
    public static boolean isFastDoubleClick(long duration) {
        long time = System.currentTimeMillis();
        if (Math.abs(time - lastClickTime) < duration) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
