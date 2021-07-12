package com.lib.block.entity.base;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 模板样式集合：所有样式字段出自此处，可通过此方法获得json串
 */
public class StyleEntity implements Serializable {
    //  -----------common style属性开始------------
    public static final String GRAVITY = "gravity";//居中方式 （center,left,right）
    public String gravity;
    public static final String MARGIN = "margin";//居中方式 （center,left,right）
    public String margin;
    public static final String PADDING = "padding";//居中方式 （center,left,right）
    public String padding;

    public static final String RADIUS = "radius";//圆角 类型int 单位dp
    public String radius;
    public static final String RADIUSCOLOR = "radiusBgColor";//圆角控件背景颜色
    public static final String IMGHEIGHT = "imgHeight";// 图片高度 类型int 单位dp
    public static final String IMGWIDTH = "imgWidth";//图片宽度 类型int 单位dp
    public static final String IMGPADDING = "imgPadding";//图片padding 类型int 单位dp
    public static final String IMGMARGIN = "imgMargin";//图片padding 类型int 单位dp
    public static final String BTNBGCOLOR = "btnBgColor";//图片padding 类型int 单位dp
    public static final String BGIMGURL = "bgImgUrl";//图片padding 类型int 单位dp
    public String bgImgUrl;
    public static final String IMGURL = "imgUrl";//图片padding 类型int 单位dp
    public String imgUrl;
    public static final String RATIO = "aspectRatio";// 宽/高的值  类型float 默认是1
    //  -----------common style属性结束------------

    //-------textView Or EditText style 属性开始----------
    public static final String TEXTSIZE = "textSize";//字体大小 类型int 单位sp
    public String textSize;

    public static final String TEXTCOLOR = "textColor";//字体颜色 类型string 例如#FFFFF
    public String textColor;
    public static final String TEXTSTYLE = "textStyle";//字体样式 类型string （italic 斜体，bold 加粗，nomal 默认）
    public String textStyle;

    public static final String LINES = "lines";//显示行数
    public String lines;

    public static final String MAXLINES = "maxLines";//显示行数
    public String maxLines;

    public static final String MINLINES = "minLines";//显示行数
    public String minLines;

    public static final String MAXWIDTH = "maxWidth";//最大宽度
    public String maxWidth;

    public static final String EMS = "ems";//
    public String ems;

    public static final String MAXEMS = "maxEms";//
    public String maxEms;

    public static final String MINEMS = "minEms";//
    public String minEms;

    public static final String ELLIPSIZE = "ellipsize";//省略位置（start,middle,end)
    public String ellipsize;
    //-------textView Or EditText style 属性结束----------


    //-------ImageView style 属性开始----------
    public static final String SCALETYPE = "scaleType";//默认值：fitcenter 图片填充类型（fitcenter,center,centerinside,centercrop,fitxy)
    public String scaleType;
    //-------ImageView style 属性结束----------


    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(String textStyle) {
        this.textStyle = textStyle;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(String maxLines) {
        this.maxLines = maxLines;
    }

    public String getMinLines() {
        return minLines;
    }

    public void setMinLines(String minLines) {
        this.minLines = minLines;
    }

    public String getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    public String getMaxEms() {
        return maxEms;
    }

    public void setMaxEms(String maxEms) {
        this.maxEms = maxEms;
    }

    public String getMinEms() {
        return minEms;
    }

    public void setMinEms(String minEms) {
        this.minEms = minEms;
    }

    public String getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(String ellipsize) {
        this.ellipsize = ellipsize;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
