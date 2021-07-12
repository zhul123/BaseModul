package com.blocks.views.floorviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.base.utils.ARouterUtils;
import com.base.utils.savedata.sp.AppSharedPreferencesHelper;
import com.blocks.views.base.BaseFloorView;
import com.component.beans.FunctionItem;
import com.component.providers.app.AppProvider;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 单图片模板
 * ratio：图片宽高比
 * radius：圆角
 * imgUrl:图片地址
 */
public class ImageFloorView extends BaseFloorView implements View.OnClickListener {
    private ImageView mImageView;
    private ViewTreeObserver vto;

    public ImageFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        mImageView = new ImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mImageView);
        vto = mImageView.getViewTreeObserver();
    }

    @Override
    protected void setCustomStyle() {

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
//        setOnHolderChildClickListener(this,mImageView);
        mImageView.setOnClickListener(this);
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                setImageRatio(cell.optDoubleParam(StyleEntity.RATIO));
                try {
                    setImgMargin(cell.optJsonArrayParam(StyleEntity.IMGMARGIN));
                } catch (JSONException e) {
                }
                loadImage(mImageView);
            }
        });
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }

    private void setImageRatio(Double ratio) {
        if (mImageView == null)
            return;
        int imgWidth = getWidth();
        int imgHeight = getHeight();
        if (!ratio.equals(Double.NaN)) {
            imgWidth = getWidth() == 0 ? dpToPx(100) : getWidth();
            imgHeight = (int) (imgWidth / ratio);
        }

        LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
        params.height = imgHeight;
        params.width = imgWidth;
        mImageView.setLayoutParams(params);
    }

    /**
     * 解决onePlusN 布局时增加边距问题。如果直接使用magin设置，会导致右边PlusN高度重叠
     * @param marginArray
     * @throws JSONException
     */
    private void setImgMargin(JSONArray marginArray) throws JSONException {
        if (mImageView == null || marginArray == null)
            return;
        LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
        switch (marginArray.length()) {
            case 1:
                int padding = marginArray.getInt(0);
                params.setMargins(padding, padding, padding, padding);
                break;
            case 2:
                int paddingLeftRight = marginArray.getInt(1);
                int paddingTopBottom = marginArray.getInt(0);
                params.setMargins(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
                break;
            case 4:
                int marginRight = marginArray.getInt(1);
                int marginTop = marginArray.getInt(0);
                int marginLeft = marginArray.getInt(3);
                int marginBottom = marginArray.getInt(2);
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                break;
        }

        mImageView.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        String arouterUrl = getOptString(Params.AROUTERURL);

        if(!TextUtils.isEmpty(arouterUrl)){
            if(arouterUrl.indexOf("webView") != -1) {
                JSONObject dataObj = getOptJsonObj(Params.DATAS);
                String datasStr = dataObj == null ? "" : dataObj.toString();
                if(TextUtils.isEmpty(datasStr))
                    return;
                //如果url中包含access_token 字段则替换为实际值
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
                String access_token = sharedPreferences.getString("access_token", null);
                sharedPreferences = null;
                datasStr = datasStr.replace("=access_token", "="+access_token);
                FunctionItem functionItem = JSON.parseObject(datasStr,FunctionItem.class);
                System.out.println("=========url:"+functionItem.url);
                ARouter.getInstance().build(arouterUrl)
                        .withSerializable(AppProvider.ConstantDef.WEBVIEW_APP,functionItem)
                        .navigation();
            }else {
                ARouterUtils.goNext(arouterUrl);
            }
        }
    }
}
