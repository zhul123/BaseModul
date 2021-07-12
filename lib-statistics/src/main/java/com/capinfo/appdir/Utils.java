package com.capinfo.appdir;

/**
 * @author :  zhulei
 * @desc :
 */
public class Utils {
    public static final String IMAGE_SUFFIX_PNG = ".png";
    public static final String IMAGE_SUFFIX_PNG_ = ".PNG";
    public static final String IMAGE_SUFFIX_JPG = ".jpg";
    public static final String IMAGE_SUFFIX_JPG_ = ".JPG";
    public static final String IMAGE_SUFFIX_JPEG = ".jpeg";
    public static final String IMAGE_SUFFIX_JPEG_ = ".JPEG";

    public static String imgSuffix(String imgUrl) {
        String suffix = "";
        if (imgUrl.contains(IMAGE_SUFFIX_PNG) || imgUrl.contains(IMAGE_SUFFIX_PNG_)) {
            suffix = IMAGE_SUFFIX_PNG;
        } else if (imgUrl.contains(IMAGE_SUFFIX_JPG) || imgUrl.contains(IMAGE_SUFFIX_JPG_)) {
            suffix = IMAGE_SUFFIX_JPG;
        } else if (imgUrl.contains(IMAGE_SUFFIX_JPEG) || imgUrl.contains(IMAGE_SUFFIX_JPEG_)) {
            suffix = IMAGE_SUFFIX_JPEG;
        } else {

        }

        return suffix;
    }
}
