package com.blocks.views.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    /**
     * 对给定的字符串进行MD5加密
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        if (!TextUtils.isEmpty(str)) {
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(str.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] byteArray = messageDigest.digest();
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            return md5StrBuff.toString();
        } else {
            return null;
        }
    }
}
