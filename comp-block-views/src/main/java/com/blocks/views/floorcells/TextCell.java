package com.blocks.views.floorcells;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.utils.ParamsUtils;

import androidx.annotation.NonNull;

public class TextCell extends BaseFloorCell<TextView> {

    @Override
    public void postBindView(@NonNull TextView view) {
        super.postBindView(view);
        view.setTextColor(getOptColor(ParamsUtils.TEXTCOLOR, Color.BLACK));
        view.setText(getOptString(ParamsUtils.TEXT));
        int textSize = getOptInt(ParamsUtils.TEXTSIZE);
        view.setTextSize(textSize == 0 ? 14 : textSize);
        int gravity = Gravity.LEFT;
        String gravityStr = getOptString(ParamsUtils.GRAVITY).toLowerCase();
        switch (gravityStr){
            case "left":
                gravity = Gravity.LEFT;
                break;
            case "right":
                gravity = Gravity.RIGHT;
                break;
            case "center":
                gravity = Gravity.CENTER;
                break;
        }
        view.setGravity(gravity);
    }

    @Override
    protected void onItemClick() {

    }
}
