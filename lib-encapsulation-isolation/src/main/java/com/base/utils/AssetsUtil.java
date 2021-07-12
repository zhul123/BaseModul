package com.base.utils;


import android.content.res.AssetManager;

import com.base.app.BaseApplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtil {
    public static String getAssetsFile(String fileName) {
        InputStream inputStream;
        AssetManager assetManager = BaseApplication.getInstance().getAssets();
        try {
            inputStream = assetManager.open(fileName);

            BufferedInputStream bis = null;
            int length;
            try {
                bis = new BufferedInputStream(inputStream);
                length = bis.available();
                byte[] data = new byte[length];
                bis.read(data);

                return new String(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
