package com.blocks.views.utils;

import android.widget.TextView;

import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONObject;

/**
 * 控件样式工具类，通过数据源中样式属性设置控件对应属性
 */
public class StyleUtils {
    //  -----------style属性开始------------
    public static final String RADIUS = "radius";//圆角 类型int 单位dp
    public static final String RADIUSCOLOR = "radiusBgColor";//圆角控件背景颜色
    public static final String IMGHEIGHT = "imgHeight";// 图片高度 类型int 单位dp
    public static final String IMGWIDTH = "imgWidth";//图片宽度 类型int 单位dp
    public static final String IMGPADDING = "imgPadding";//图片padding 类型int 单位dp
    public static final String IMGMARGIN = "imgMargin";//图片padding 类型int 单位dp
    public static final String TEXTSIZE = "textSize";//字体大小 类型int 单位sp
    public static final String TEXTCOLOR = "textColor";//字体颜色 类型string 例如#FFFFF
    public static final String TEXTSTYLE = "textStyle";//字体样式 类型string （italic 斜体，bold 加粗，nomal 默认）
    public static final String GRAVITY = "gravity";//居中方式 （center,left,right）
    public static final String RATIO = "aspectRatio";// 宽/高的值  类型float 默认是1
    //  -----------style属性结束------------
    private static StyleUtils instance = null;

    public static StyleUtils getInstance(){
        if(instance == null){
            synchronized (StyleUtils.class){
                if(instance == null){
                    instance = new StyleUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 设置TextView样式
     * @param mTextView
     * @param mBaseCell
     */
    private void setTextViewStyle(TextView mTextView , BaseCell mBaseCell){
        if(mTextView == null || mBaseCell == null)
            return;
        JSONObject styleObject = mBaseCell.extras.optJSONObject(ParamsUtils.STYLE);
        if(styleObject == null)
            return;

    }
}
