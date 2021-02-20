package com.blocks.views.floorcells;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.utils.ParamsUtils;
import com.blocks.views.utils.StyleUtils;

import androidx.annotation.NonNull;

public class TextCell extends BaseFloorCell<TextView> {

    @Override
    public void postBindView(@NonNull TextView view) {
        super.postBindView(view);
        view.setText(getOptString(ParamsUtils.TEXT));
//        int textSize = getOptInt(ParamsUtils.TEXTSIZE);
//        view.setTextSize(textSize == 0 ? 14 : textSize);
        StyleUtils.getInstance().setTextViewStyle(view,this);
    }

    @Override
    protected void onItemClick() {

    }
}
