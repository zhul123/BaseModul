package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.blocks.views.base.BaseFloorView;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

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
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);

        mTextView.setTextColor(getOptColor(StyleEntity.TEXTCOLOR,DEFAULTCOLOR));
        int dp10 = dpToPx(10);
        int dp5 = dpToPx(5);
        mTextView.setPadding(dp10,dp5,dp10,dp5);
        int textSize = getOptInt(StyleEntity.TEXTSIZE);
        if(textSize > 0) {
            mTextView.setTextSize(textSize);
        }else{
            mTextView.setTextSize(DEFAULTSIZE);
        }

        mTextView.setText(getOptString(Params.TEXT));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
