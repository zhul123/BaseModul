package com.blocks.views.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.imagehelper.ImageHelper;
import com.blocks.views.BuildConfig;
import com.blocks.views.R;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public abstract class BaseFloorCell<T extends View> extends BaseCell<T> implements View.OnClickListener {


    protected Context mContext;
    protected OnChildClickListener onChildClickLsn;//子view点击监听接口
    private int DEFAULTTEXTCOLOR = Color.BLACK;
    private int DEFAULTTEXTSIZE = 14;

    protected abstract void onItemClick();

    @Override
    public void postBindView(@NonNull T view) {
        super.postBindView(view);
        mContext = view.getContext();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof BaseFloorView) {
            onItemClick();
        } else {
            if (onChildClickLsn != null) {
                onChildClickLsn.onChildClick(view, this);
            }
        }
    }


    public void setLsn() {
        if (onChildClickLsn == null) {
//            setOnClickListener(this);
        }
    }


    /**
     * 在holder中处理子view的点击事件
     *
     * @param onChildClickLsn
     * @param views
     */
    public void setOnHolderChildClickListener(OnChildClickListener onChildClickLsn, View... views) {
        this.onChildClickLsn = onChildClickLsn;
        try {
            Iterator<View> iterator = Arrays.asList(views).iterator();
            while (iterator.hasNext()) {
                iterator.next().setOnClickListener(this);
            }
        } catch (Exception e) {

        }
    }

    public interface OnChildClickListener {
        void onChildClick(View view, BaseCell cell);
    }

    public static boolean isBase64Img(String imgurl) {
        if (!TextUtils.isEmpty(imgurl) && (imgurl.startsWith("data:image/png;base64,")
                || imgurl.startsWith("data:image/*;base64,") || imgurl.startsWith("data:image/jpg;base64,")
        )) {
            return true;
        }
        return false;
    }

    /**
     * 设置view的LayoutParams属性
     *
     * @param v
     */
    protected void setImgLayoutParams(View v) {
        if (v == null)
            return;
        int width = dpToPx(getOptInt(ParamsUtils.IMGWIDTH));
        int height = dpToPx(getOptInt(ParamsUtils.IMGHEIGHT));
        if (width == 0 && height != 0) {
            width = height;
        }
        if (height == 0 && width != 0) {
            height = width;
        }
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams != null) {
            if (width > 0 && height > 0) {
                layoutParams.width = width;
                layoutParams.height = height;
            } else {
                layoutParams.width = dpToPx(45);
                layoutParams.height = dpToPx(45);
            }
            v.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置图片样式及加载图片
     * radius圆角
     *
     * @param imageView
     */
    protected void setOptImageStyle(ImageView imageView) {
        setImgLayoutParams(imageView);
    }

    protected void loadImage(ImageView imageView) {
        loadImage(imageView, ParamsUtils.IMGURL, ParamsUtils.RADIUS);
    }

    /**
     * imageview 加载图片
     * radius圆角
     *
     * @param imageView
     * @param imgUrlKey
     */
    protected void loadImage(ImageView imageView, String imgUrlKey, String radiusKey) {
        if (imageView == null)
            return;
        System.out.println(String.format("========loadImage;name:%s", optStringParam(ParamsUtils.TEXT)));
//            ImageHelper.getInstance().setRadiusDrawable(imageView
//                    , getOptString(imgUrlKey), R.drawable.floor_img_default, getOptInt(radiusKey));
        ImageHelper.getInstance().setCommImage(getOptString(imgUrlKey), imageView, R.drawable.floor_img_default);

    }

    /**
     * 设置配置的字体样式
     * textColor,textSize,textStyle
     *
     * @param textView
     */
    protected void setOptTextStyle(TextView textView) {
        if (textView == null)
            return;

        int textColor = getOptColor(ParamsUtils.TEXTCOLOR, 0);
        if (textColor != 0) {
            textView.setTextColor(textColor);
        } else {
            textView.setTextColor(DEFAULTTEXTCOLOR);
        }

        float textSize = (float) getOptDouble(ParamsUtils.TEXTSIZE);
        if (textSize > 0) {
            textView.setTextSize(textSize);
        } else {
            textView.setTextSize(DEFAULTTEXTSIZE);
        }

        switch (getOptString(ParamsUtils.TEXTSTYLE)) {
            case "bold":
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case "italic":
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                break;
            default:
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
        }
    }

    /**
     * 设置数据源datas中的参数值
     *
     * @param paramKey
     */
    protected void setDataStringValue(String paramKey, String paramValue) {
        if (TextUtils.isEmpty(paramKey))
            return;
        try {
            JSONObject datasObject = extras.optJSONObject(ParamsUtils.DATAS);
            if (datasObject == null) {
                datasObject = new JSONObject();
                extras.put(ParamsUtils.DATAS, datasObject);
            }

            datasObject.put(paramKey, paramValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置数据源AllBizParams中的参数值
     *
     * @param paramKey
     * @param paramValue
     */
    protected void setBizParams(String paramKey, String paramValue) {

    }

    protected int dpToPx(double dp) {
        return Style.dp2px(dp);
    }

    protected int getOptColor(String key) {
        return getOptColor(key, Color.WHITE);
    }

    protected int getOptColor(String key, int defaultColor) {
        String colorStr = getOptString(key);
        if (TextUtils.isEmpty(colorStr)) {
            return defaultColor;
        } else {
            return Style.parseColor(colorStr, defaultColor);
        }
    }

    protected String getOptString(String key) {
        String backStr = "";
        if (TextUtils.isEmpty(key)) {
        } else {
            backStr = optStringParam(key);
            if (TextUtils.isEmpty(backStr)) {
                if (optJsonObjectParam(ParamsUtils.DATAS) != null) {
                    backStr = optJsonObjectParam(ParamsUtils.DATAS).optString(key);
                } else if (optJsonObjectParam(ParamsUtils.STYLE) != null) {
                    backStr = optJsonObjectParam(ParamsUtils.STYLE).optString(key);
                }
            }
        }
        return backStr;
    }

    protected String getOptString(BaseCell cell, String key) {
        String backStr = "";
        if (cell == null || TextUtils.isEmpty(key)) {
        } else {
            backStr = cell.optStringParam(key);
            if (TextUtils.isEmpty(backStr) && cell.optJsonObjectParam(ParamsUtils.DATAS) != null) {
                backStr = cell.optJsonObjectParam(ParamsUtils.DATAS).optString(key);
            }
        }
        return backStr;
    }

    protected int getOptInt(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        } else {
            return optIntParam(key);
        }
    }

    protected double getOptDouble(String key) {
        return optDoubleParam(key);
    }

    protected boolean getOptBoolean(String key) {
        return optBoolParam(key);
    }

    protected long getOptLong(String key) {
        return optLongParam(key);
    }

    protected JSONObject getOptJsonObj(String key) {
        return optJsonObjectParam(key);
    }

    protected JSONArray getOptJsonArray(String key) {
        JSONArray jsonArray;
        jsonArray = optJsonArrayParam(key);
        if (jsonArray == null && optJsonObjectParam(ParamsUtils.DATAS) != null) {
            jsonArray = optJsonObjectParam(ParamsUtils.DATAS).optJSONArray(key);
        }
        return jsonArray;
    }

    public void print(String str) {
        if (BuildConfig.DEBUG) {
            System.out.println("========" + str);
        }
    }
}
