package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blocks.views.R;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

/**
 * 单文字模板
 * ratio：图片宽高比
 * radius：圆角
 * imgUrl:图片地址
 */
public class CheckBoxTextFloorView extends BaseFloorView {
    private static final int DEFAULTCOLOR = Color.BLACK;
    private static final int DEFAULTSIZE = 14;
    private static final int DEFAULTPADDING = 10;
    private View mView;
    private TextView mTextView;
    private CheckBox mCheckBox;

    public CheckBoxTextFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.floor_choose_text,null);
        mCheckBox = mView.findViewById(R.id.cb_checkbox);
        mTextView = mView.findViewById(R.id.tv_text);
        addView(mView);
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
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDataStringValue(Params.CHECKSTATE,isChecked+"");
            }
        });
        mTextView.setText(getOptString(Params.TEXT));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
