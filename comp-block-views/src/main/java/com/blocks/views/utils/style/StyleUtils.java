package com.blocks.views.utils.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blocks.views.utils.ParamsUtils;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONObject;

/**
 * 控件样式工具类，通过数据源中样式属性设置控件对应属性
 */
public class StyleUtils {
    private static final int DEFAULTMAX = Integer.MAX_VALUE;
    private static final int DEFAULTIN = -1;
    private static final int DEFAULTLINES = 1;
    private static final int DEFAULTTEXTSIZE = 14;
    private static final int DEFAULTTEXTCOLOR = Color.parseColor("#333333");
    private static final int DEFAULTBGCOLOR = Color.WHITE;

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
        mTextView.setTextSize(styleObject.optInt(StyleEntity.TEXTSIZE,DEFAULTTEXTSIZE));
        //设置文字颜色
        String colorStr = styleObject.optString(StyleEntity.TEXTCOLOR);
        mTextView.setTextColor(Style.parseColor(colorStr, DEFAULTTEXTCOLOR));
        //设置lines
        mTextView.setMaxLines(styleObject.optInt(StyleEntity.MAXLINES,DEFAULTMAX));
        mTextView.setMinLines(styleObject.optInt(StyleEntity.MINLINES,DEFAULTLINES));
        mTextView.setLines(styleObject.optInt(StyleEntity.LINES,DEFAULTLINES));
        //设置width
        mTextView.setMaxWidth(styleObject.optInt(StyleEntity.MAXWIDTH,DEFAULTMAX));
        //设置ems
//        mTextView.setMaxEms(styleObject.optInt(StyleEntity.MAXEMS,DEFAULTMAX));
//        mTextView.setMinEms(styleObject.optInt(StyleEntity.MINEMS,DEFAULTIN));
//        mTextView.setEms(styleObject.optInt(StyleEntity.EMS,DEFAULTIN));

        //设置ellipsize
        switch (styleObject.optString(StyleEntity.ELLIPSIZE).toLowerCase()) {
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
                mTextView.setEllipsize(TextUtils.TruncateAt.END);
                break;
        }

        //设置文字样式
        switch (styleObject.optString(StyleEntity.TEXTSTYLE).toLowerCase()) {
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

        switch (styleObject.optString(StyleEntity.GRAVITY).toLowerCase()){
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
        mEditText.setTextSize(styleObject.optInt(StyleEntity.TEXTSIZE,DEFAULTTEXTSIZE));
        //设置文字颜色
        String colorStr = styleObject.optString(StyleEntity.TEXTCOLOR);
        mEditText.setTextColor(Style.parseColor(colorStr, DEFAULTTEXTCOLOR));
        //设置lines
        mEditText.setLines(styleObject.optInt(StyleEntity.LINES,DEFAULTLINES));
        mEditText.setMaxLines(styleObject.optInt(StyleEntity.MAXLINES,DEFAULTMAX));
        mEditText.setMinLines(styleObject.optInt(StyleEntity.MINLINES,DEFAULTLINES));
        //设置width
        mEditText.setMaxWidth(styleObject.optInt(StyleEntity.MAXWIDTH,DEFAULTMAX));
        //设置ems
        mEditText.setEms(styleObject.optInt(StyleEntity.EMS,DEFAULTLINES));
        mEditText.setMaxEms(styleObject.optInt(StyleEntity.MAXEMS,DEFAULTLINES));
        mEditText.setMinEms(styleObject.optInt(StyleEntity.MINEMS,DEFAULTLINES));
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
        switch (styleObject.optString(StyleEntity.SCALETYPE).toLowerCase()) {
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
     * 设置Button样式
     * @param mButton
     * @param mBaseCell
     */
    public void setButtonStyle(Button mButton, BaseCell mBaseCell){
        if(mButton == null)
            return;
        //防止因cell为null导致样式错乱
        if(mBaseCell == null) {
            mBaseCell = new BaseCell();
        }
        JSONObject styleObject = initStyleObject(mBaseCell);

        mButton.setBackgroundColor(Style.parseColor(styleObject.optString(StyleEntity.BTNBGCOLOR),DEFAULTBGCOLOR));

    }

    /**
     * 初始化styleObject 防止为null时item样式错乱
     * @param mBaseCell
     * @return
     */
    private JSONObject initStyleObject(BaseCell mBaseCell){
        JSONObject styleObject = mBaseCell.extras.optJSONObject(Params.STYLE);
        //防止item样式错乱
        if(styleObject == null){
            styleObject = new JSONObject();
        }
        return styleObject;
    }
}
