package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 单文字模板
 * ratio：图片宽高比
 * radius：圆角
 * imgUrl:图片地址
 */
public class TextFloorView extends BaseFloorView {
    private static final int DEFAULTCOLOR = Color.BLACK;
    private static final int DEFAULTSIZE = 14;
    private static final int DEFAULTPADDING = 10;
    private TextView mTextView;

    public TextFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        mTextView = new TextView(getContext());
        addView(mTextView);
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

        mTextView.setTextColor(getOptColor(ParamsUtils.TEXTCOLOR,DEFAULTCOLOR));
        int dp10 = dpToPx(10);
        int dp5 = dpToPx(5);
        mTextView.setPadding(dp10,dp5,dp10,dp5);
        int textSize = getOptInt(ParamsUtils.TEXTSIZE);
        if(textSize > 0) {
            mTextView.setTextSize(textSize);
        }else{
            mTextView.setTextSize(DEFAULTSIZE);
        }

        mTextView.setText(getOptString(ParamsUtils.TEXT));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
