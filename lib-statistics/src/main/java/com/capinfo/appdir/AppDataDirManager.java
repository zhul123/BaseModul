package com.capinfo.appdir;

import android.content.Context;

import java.io.File;

/**
 * 缓存路径管理类
 * Created by zhulei
 */

public class AppDataDirManager {

    /**
     * JLogger Root 目录
     * @param context
     * @return
     */
    public static File getCacheJLoggerRoot(Context context){
        File file = AppDataDir.getInstance().getCacheDirectory(context,"");
        return file;
    }

    /**
     * JLogger 绝对路径
     * @param context
     * @return
     */
    public static File getCacheJLogger(Context context) {
//        String tmpPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+ getCacheJLoggerInitXLog(context);
//        return new File(tmpPath);
        File file = AppDataDir.getInstance().getCacheDirectory(context, getCacheJLoggerInitXLog(context).getAbsolutePath());
        return file;
    }

    public static File getCacheDirJLogger(Context context){
        return new File(AppDataDir.getInstance().getCacheDir(context).getAbsolutePath() + File.separator +  getCacheJLoggerInitXLog(context).getAbsolutePath());
    }

    /**
     * JLogger Xlog的相对路径
     * @param context
     * @return
     */
    public static File getCacheJLoggerInitXLog(Context context) {
        String tmpPath = AppDataDirConstant.PAH_ROOT_FOLDER + File.separator + AppDataDirConstant.CACHE_JLOGGER;
        return new File(tmpPath);
    }

    /**
     * 下载apk
     *
     * @param context
     * @return
     */
    public static File getDownloadApk(Context context) {
        return AppDataDir.getInstance().getCacheDirectory(context, AppDataDirConstant.CACHE_DOWNLOAD_APK);
    }

    /**
     * 下载pdf
     *
     * @param context
     * @param dir
     * @return
     */
    public static File getDownloadPdf(Context context, String dir) {
        return AppDataDir.getInstance().getCacheDirectory(context, AppDataDirConstant.CACHE_DOWNLOAD_PDF + dir);
    }

    /**
     * 头像裁剪
     *
     * @param context
     * @param picture
     * @return
     */
    public static File getAvatarCropPhoto(Context context, String picture) {

        String photoCompressPath = AppDataDirConstant.CACHE_PHOTO;
        File file = AppDataDir.getInstance().getCacheDirectory(context, photoCompressPath);
        String cropImagePath = file.getAbsolutePath() + File.separator + System.currentTimeMillis() + Utils.imgSuffix(picture);
        return new File(cropImagePath);
    }

    /**
     * 图片压缩
     *
     * @param context
     * @return
     */
    public static File getCompressPhoto(Context context) {
        String photoCompressPath = AppDataDirConstant.CACHE_PHOTO;
        File cacheDir = AppDataDir.getInstance().getCacheDirectory(context, photoCompressPath);
        return cacheDir;
    }

    /**
     * 保存图片路径，永久缓存
     *
     * @param context
     * @return
     */
    public static File getSavePhoto(Context context) {
//        return AppDataDir.getInstance().getSdcardDirectory(context, AppDataDirConstant.CACHE_DOWNLOAD_PHOTO);
        return AppDataDir.getInstance().getCacheDirectory(context, AppDataDirConstant.CACHE_DOWNLOAD_PHOTO);
    }

    /**
     * 网络返回缓存
     *
     * @param context
     * @return
     */
    public static File getNetworkCache(Context context) {
        String path = AppDataDirConstant.CACHE;
        File cacheDir = AppDataDir.getInstance().getCacheDirectory(context, path);
        return cacheDir;
    }

    /**
     * 相机拍照
     *
     * @param context
     * @return
     */
    public static File getCameraPhoto(Context context, String fileName) {
        String photoCompressPath = AppDataDirConstant.CAMERA_PHOTO;
//        File file = AppDataDir.getInstance().getSdcardDirectory(context, photoCompressPath);
        File file = AppDataDir.getInstance().getCacheDirectory(context, photoCompressPath);
        return new File(file.getAbsolutePath() + "/" + fileName);
    }

    public static File getFileDir(Context context){
        return AppDataDir.getInstance().getFileDir(context);
    }

    public static File getCacheDirPath(Context context) {
        return AppDataDir.getInstance().getCacheDirectory(context, "");
    }

    /**
     * 获取缓存目录大小（bytes单位）
     *
     * @param context
     * @return
     */
    public static long getAppDataDirSize(Context context) {
        return AppDataDir.getInstance().getAppDataDirSize(context);
    }

    /**
     * 删除缓存目录
     *
     * @param context
     */
    public static void delAllFiles(Context context) {
        AppDataDir.getInstance().delAllFiles(context);
    }
    /**
     *
     * 当本地存储大数据的时候，不使用sp，使用File进行存储到本地
     * @param context
     * */
    public static File getLocalDataCache(Context context){
        String path = AppDataDirConstant.CACHE_LOCAL_DATA;
        File cacheDir = AppDataDir.getInstance().getCacheDirectory(context, path);
        return cacheDir;
    }

    public static File getDcimDir(Context context) {
        return AppDataDir.getInstance().getSdcardDirectory(context, AppDataDirConstant.DCIM_ROOT_DIR);
    }
}
