package com.blocks.views.base;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.base.imagehelper.ImageHelper;
import com.blocks.BuildConfig;
import com.blocks.R;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

import androidx.annotation.NonNull;

public abstract class BaseFloorCell<T extends View> extends BaseCell<T> implements View.OnClickListener {


    protected Context mContext;
    protected OnChildClickListener onChildClickLsn;//子view点击监听接口
    private int DEFAULTTEXTCOLOR = Color.BLACK;
    private int DEFAULTTEXTSIZE = 14;

    @Override
    public void postBindView(@NonNull T view) {
        super.postBindView(view);
        mContext = view.getContext();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (onChildClickLsn != null) {
            onChildClickLsn.onChildClick(view, this);
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
        int width = dpToPx(getOptInt(StyleEntity.IMGWIDTH));
        int height = dpToPx(getOptInt(StyleEntity.IMGHEIGHT));
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
        loadImage(imageView, Params.IMGURL, StyleEntity.RADIUS);
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
//            ImageHelper.getInstance().setRadiusDrawable(imageView
//                    , getOptString(imgUrlKey), R.drawable.floor_img_default, getOptInt(radiusKey));
        ImageHelper.getInstance().setCommImage(getOptString(imgUrlKey), imageView, R.drawable.floor_img_default);

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
            JSONObject datasObject = extras.optJSONObject(Params.DATAS);
            if (datasObject == null) {
                datasObject = new JSONObject();
                extras.put(Params.DATAS, datasObject);
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
                if (optJsonObjectParam(Params.DATAS) != null) {
                    backStr = optJsonObjectParam(Params.DATAS).optString(key);
                } else if (optJsonObjectParam(Params.STYLE) != null) {
                    backStr = optJsonObjectParam(Params.STYLE).optString(key);
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
            if (TextUtils.isEmpty(backStr) && cell.optJsonObjectParam(Params.DATAS) != null) {
                backStr = cell.optJsonObjectParam(Params.DATAS).optString(key);
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
        if (jsonArray == null && optJsonObjectParam(Params.DATAS) != null) {
            jsonArray = optJsonObjectParam(Params.DATAS).optJSONArray(key);
        }
        return jsonArray;
    }

    public void print(String str) {
        if (BuildConfig.DEBUG) {
            System.out.println("========" + str);
        }
    }
}
