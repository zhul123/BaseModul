package com.blocks.views.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONObject;

/**
 * 控件样式工具类，通过数据源中样式属性设置控件对应属性
 */
public class StyleUtils {
    private static final int DEFAULTMAX = Integer.MAX_VALUE;
    private static final int DEFAULTLINES = 1;
    private static final int DEFAULTTEXTSIZE = 14;
    private static final int DEFAULTTEXTCOLOR = Color.parseColor("#333333");
    //  -----------style属性开始------------
    public static final String RADIUS = "radius";//圆角 类型int 单位dp
    public static final String RADIUSCOLOR = "radiusBgColor";//圆角控件背景颜色
    public static final String IMGHEIGHT = "imgHeight";// 图片高度 类型int 单位dp
    public static final String IMGWIDTH = "imgWidth";//图片宽度 类型int 单位dp
    public static final String IMGPADDING = "imgPadding";//图片padding 类型int 单位dp
    public static final String IMGMARGIN = "imgMargin";//图片padding 类型int 单位dp
    public static final String SCALETYPE = "scaleType";//图片填充类型（
    public static final String TEXTSIZE = "textSize";//字体大小 类型int 单位sp
    public static final String TEXTCOLOR = "textColor";//字体颜色 类型string 例如#FFFFF
    public static final String TEXTSTYLE = "textStyle";//字体样式 类型string （italic 斜体，bold 加粗，nomal 默认）
    public static final String LINES = "lines";//显示行数
    public static final String MAXLINES = "maxLines";//显示行数
    public static final String MINLINES = "minLines";//显示行数
    public static final String MAXWIDTH = "maxWidth";//最大宽度
    public static final String EMS = "ems";//
    public static final String MAXEMS = "maxEms";//
    public static final String MINEMS = "minEms";//
    public static final String ELLIPSIZE = "ellipsize";//省略位置（start,middle,end)
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
    public void setTextViewStyle(TextView mTextView , BaseCell mBaseCell){
        if(mTextView == null)
            return;
        //防止因cell为null导致样式错乱
        if(mBaseCell == null) {
            mBaseCell = new BaseCell();
        }
        JSONObject styleObject = initStyleObject(mBaseCell);

        //设置文字大小
        mTextView.setTextSize(styleObject.optInt(TEXTSIZE,DEFAULTTEXTSIZE));
        //设置文字颜色
        String colorStr = styleObject.optString(TEXTCOLOR);
        mTextView.setTextColor(Style.parseColor(colorStr, DEFAULTTEXTCOLOR));
        //设置lines
        mTextView.setLines(styleObject.optInt(LINES,DEFAULTLINES));
        mTextView.setMaxLines(styleObject.optInt(MAXLINES,DEFAULTMAX));
        mTextView.setMinLines(styleObject.optInt(MINLINES,DEFAULTLINES));
        //设置width
        mTextView.setMaxWidth(styleObject.optInt(MAXWIDTH,DEFAULTMAX));
        //设置ems
        mTextView.setEms(styleObject.optInt(EMS,DEFAULTLINES));
        mTextView.setMaxEms(styleObject.optInt(MAXEMS,DEFAULTLINES));
        mTextView.setMinEms(styleObject.optInt(MINEMS,DEFAULTLINES));

        //设置ellipsize
        switch (styleObject.optString(ELLIPSIZE).toLowerCase()) {
            case "start":
                mTextView.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case "middle":
                mTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case "end":
                mTextView.setEllipsize(TextUtils.TruncateAt.END);
                break;
            default:
                mTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                break;
        }

        //设置文字样式
        switch (styleObject.optString(TEXTSTYLE).toLowerCase()) {
            case "bold":
                mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case "italic":
                mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                break;
            default:
                mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
        }

        switch (styleObject.optString(GRAVITY).toLowerCase()){
            case "left":
                mTextView.setGravity(Gravity.LEFT);
                break;
            case "right":
                mTextView.setGravity(Gravity.RIGHT);
                break;
            case "center":
                mTextView.setGravity(Gravity.CENTER);
                break;
            default:
                mTextView.setGravity(Gravity.LEFT);
                break;
        }
    }

    /**
     * 设置EditText样式
     * @param mEditText
     * @param mBaseCell
     */
    public void setEditTextStyle(EditText mEditText, BaseCell mBaseCell){
        if(mEditText == null)
            return;
        //防止因cell为null导致样式错乱
        if(mBaseCell == null) {
            mBaseCell = new BaseCell();
        }
        JSONObject styleObject = initStyleObject(mBaseCell);

        //设置文字大小
        mEditText.setTextSize(styleObject.optInt(TEXTSIZE,DEFAULTTEXTSIZE));
        //设置文字颜色
        String colorStr = styleObject.optString(TEXTCOLOR);
        mEditText.setTextColor(Style.parseColor(colorStr, DEFAULTTEXTCOLOR));
        //设置lines
        mEditText.setLines(styleObject.optInt(LINES,DEFAULTLINES));
        mEditText.setMaxLines(styleObject.optInt(MAXLINES,DEFAULTMAX));
        mEditText.setMinLines(styleObject.optInt(MINLINES,DEFAULTLINES));
        //设置width
        mEditText.setMaxWidth(styleObject.optInt(MAXWIDTH,DEFAULTMAX));
        //设置ems
        mEditText.setEms(styleObject.optInt(EMS,DEFAULTLINES));
        mEditText.setMaxEms(styleObject.optInt(MAXEMS,DEFAULTLINES));
        mEditText.setMinEms(styleObject.optInt(MINEMS,DEFAULTLINES));
    }

    /**
     * 设置ImageView样式
     * @param mImageView
     * @param mBaseCell
     */
    public void setImageViewStyle(ImageView mImageView, BaseCell mBaseCell){
        if(mImageView == null)
            return;
        //防止因cell为null导致样式错乱
        if(mBaseCell == null) {
            mBaseCell = new BaseCell();
        }
        JSONObject styleObject = initStyleObject(mBaseCell);
        switch (styleObject.optString(SCALETYPE).toLowerCase()) {
            case "fitcenter":
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case "center":
                mImageView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case "centerinside":
                mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
            case "centercrop":
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case "fitxy":
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            default:
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
        }
    }

    /**
     * 初始化styleObject 防止为null时item样式错乱
     * @param mBaseCell
     * @return
     */
    private JSONObject initStyleObject(BaseCell mBaseCell){
        JSONObject styleObject = mBaseCell.extras.optJSONObject(ParamsUtils.STYLE);
        //防止item样式错乱
        if(styleObject == null){
            styleObject = new JSONObject();
        }
        return styleObject;
    }
}
