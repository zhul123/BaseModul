package com.capinfo.appdir;

import java.io.File;

/**
 * 应用保存路径常量
 * Created by zhulei
 */
public class AppDataDirConstant {

    public static final String APP_ROOT_DIR = "CAP";

    /**apk下载目录*/
    public static final String CACHE_DOWNLOAD_APK = "CAP_Apk";
    /**pdf下载目录*/
    public static final String CACHE_DOWNLOAD_PDF = "CAP_Pdf";
    /**图片缓存路径*/
    public static final String CACHE_PHOTO = "CAP_Photo";
    /**日志保存路径*/
    public static final String CACHE_JLOGGER = "CAP_JLogger";
    /**theme下载目录*/
    public static final String CACHE_DOWNLOAD_THEME = APP_ROOT_DIR + File.separator + "CAP_Theme";
    /**图片下载目录*/
    public static final String CACHE_DOWNLOAD_PHOTO = APP_ROOT_DIR + File.separator + CACHE_PHOTO;
    /**网络缓存目录*/
    public static final String CACHE = "CAP_NetowrkCache";

    /**照相机照完的照片*/
    public static final String CAMERA_PHOTO = APP_ROOT_DIR + File.separator + "CAP_Photo";
    /**本都存储的文件路径*/
    public static final String CACHE_LOCAL_DATA = "CAP_Local_Data";

    //相册路径
    public static final String DCIM_ROOT_DIR = "DCIM";


    public static final String PAH_ROOT_FOLDER = "Cap";

   /**埋点路径*/
    public static final String LOG_FILE_FOLDER = "capinfo/log";
    /**埋点文件*/
    public static final String LOG_FILE_NAME = "capinfo.log";
    /**上传埋点文件的副本*/
    public static final String UPLOAD_LOG_FILE_NAME = "upload_capiinfo.log";

}
