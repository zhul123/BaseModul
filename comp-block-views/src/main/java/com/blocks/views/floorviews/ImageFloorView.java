package com.blocks.views.floorviews;

import android.content.Context;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 单图片模板
 * ratio：图片宽高比
 * radius：圆角
 * imgUrl:图片地址
 */
public class ImageFloorView extends BaseFloorView {
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
    public void onItemClick() {

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);

        mImageView.post(new Runnable() {
            @Override
            public void run() {
                setImageRatio(cell.optDoubleParam(ParamsUtils.RATIO));
                try {
                    setImgMargin(cell.optJsonArrayParam(ParamsUtils.IMGMARGIN));
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
        RelativeLayout.LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
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
}
