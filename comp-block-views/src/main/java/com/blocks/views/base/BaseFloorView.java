package com.blocks.views.base;

import android.content.Context;
import android.graphics.Color;
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
import com.blocks.views.utils.style.StyleUtils;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

import androidx.annotation.RequiresApi;

public abstract class BaseFloorView<T extends ViewGroup.LayoutParams> extends RelativeLayout implements ITangramViewLifeCycle, View.OnClickListener {


    protected Context mContext;
    protected BaseCell mBaseCell;
    protected OnChildClickListener onChildClickLsn;//子view点击监听接口
    private int DEFAULTTEXTCOLOR = Color.BLACK;
    private int DEFAULTTEXTSIZE = 14;

    public BaseFloorView(Context context) {
        super(context);
        this.mContext = context;
        init();
        setLsn();
    }

    public BaseFloorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
        setLsn();
    }

    public BaseFloorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
        setLsn();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseFloorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init();
        setLsn();
    }

    @Override
    public void cellInited(BaseCell cell) {
        mBaseCell = cell;
        setOnClickListener(cell);
    }

    @Override
    public void postBindView(BaseCell cell) {
        if (cell == null)
            return;
        this.mBaseCell = cell;
        setCustomStyle();
    }

    protected abstract void init();

    protected abstract void setCustomStyle();

    protected abstract void onItemClick();

    @Override
    public void onClick(View view) {
        if (view instanceof BaseFloorView) {
            onItemClick();
        } else {
            if (onChildClickLsn != null) {
                onChildClickLsn.onChildClick(view, mBaseCell);
            }
        }
    }

    protected void addViewByWrap(View view) {
        if (view != null) {
            addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
    }

    public void setLsn() {
        if(onChildClickLsn == null) {
            setOnClickListener(this);
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

    public static boolean isBase64Img(String imgurl){
        if(!TextUtils.isEmpty(imgurl)&&(imgurl.startsWith("data:image/png;base64,")
                ||imgurl.startsWith("data:image/*;base64,")||imgurl.startsWith("data:image/jpg;base64,")
        ))
        {
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
        T layoutParams = (T) v.getLayoutParams();
        if (layoutParams != null) {
            if (width > 0 && height > 0) {
                layoutParams.width = width;
                layoutParams.height = height;
            }else{
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
            ImageHelper.getInstance().setRadiusDrawable(imageView
                    , getOptString(imgUrlKey), R.drawable.floor_img_default, getOptInt(radiusKey));
//            ImageHelper.getInstance().setCommImage(getOptString(imgUrlKey),imageView, R.drawable.floor_img_default);

    }

    /**
     * 设置配置的字体样式
     * textColor,textSize,textStyle
     *
     * @param textView
     */
    protected void setOptTextStyle(TextView textView) {
        StyleUtils.getInstance().setTextViewStyle(textView,mBaseCell);
    }

    /**
     * 设置数据源datas中的参数值
     * @param paramKey
     */
    protected void setDataStringValue(String paramKey,String paramValue){
        if(TextUtils.isEmpty(paramKey))
            return;
        try{
            JSONObject datasObject = mBaseCell.extras.optJSONObject(Params.DATAS);
            if(datasObject == null){
                datasObject = new JSONObject();
                mBaseCell.extras.put(Params.DATAS,datasObject);
            }

            datasObject.put(paramKey,paramValue);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置数据源AllBizParams中的参数值
     * @param paramKey
     * @param paramValue
     */
    protected void setBizParams(String paramKey,String paramValue){

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
        if (mBaseCell == null || TextUtils.isEmpty(key)) {
        } else {
            backStr = mBaseCell.optStringParam(key);
            if (TextUtils.isEmpty(backStr)) {
                if(mBaseCell.optJsonObjectParam(Params.DATAS) != null) {
                    backStr = mBaseCell.optJsonObjectParam(Params.DATAS).optString(key);
                }else if(mBaseCell.optJsonObjectParam(Params.STYLE) != null){
                    backStr = mBaseCell.optJsonObjectParam(Params.STYLE).optString(key);
                }
            }
        }
        return backStr;
    }
    protected String getOptString(BaseCell cell , String key) {
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
        if (mBaseCell == null || TextUtils.isEmpty(key)) {
            return 0;
        } else {
            return mBaseCell.optIntParam(key);
        }
    }

    protected double getOptDouble(String key) {
        if (mBaseCell == null) {
            return 0;
        } else {
            return mBaseCell.optDoubleParam(key);
        }
    }

    protected boolean getOptBoolean(String key) {
        if (mBaseCell == null) {
            return false;
        } else {
            return mBaseCell.optBoolParam(key);
        }
    }

    protected long getOptLong(String key) {
        if (mBaseCell == null) {
            return 0;
        } else {
            return mBaseCell.optLongParam(key);
        }
    }

    protected JSONObject getOptJsonObj(String key) {
        if (mBaseCell == null) {
            return null;
        } else {
            return mBaseCell.optJsonObjectParam(key);
        }
    }

    protected JSONArray getOptJsonArray(String key) {
        JSONArray jsonArray ;
        if (mBaseCell == null) {
            return null;
        } else {
            jsonArray = mBaseCell.optJsonArrayParam(key);
            if( jsonArray == null && mBaseCell.optJsonObjectParam(Params.DATAS) != null) {
                jsonArray = mBaseCell.optJsonObjectParam(Params.DATAS).optJSONArray(key);
            }
            return jsonArray;
        }
    }

    public void print(String str){
        if(BuildConfig.DEBUG){
            System.out.println("========"+str);
        }
    }
}
