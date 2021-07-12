package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blocks.R;
import com.blocks.views.base.BaseFloorView;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @Auter zhulei
 * Footer模板
 * radius：背景圆角
 * radiusBgColor：圆角控件背景颜色
 * text:标题
 */
public class FooterFloorView extends BaseFloorView {
    private TextView tv_title;
    private View footerView;
    private GradientDrawable drawable;
    private int radius = 10;

    public FooterFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        footerView = LayoutInflater.from(mContext).inflate(R.layout.floor_footer, null);
        addView(footerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv_title = findViewById(R.id.tv_title);
        drawable = new GradientDrawable();
    }

    @Override
    protected void setCustomStyle() {
        if (drawable == null) {
            return;
        }
        //兼容radius为整数情况
        if (getOptJsonArray(StyleEntity.RADIUS) != null) {
            setRadius(getOptJsonArray(StyleEntity.RADIUS));
        } else {
            setRadius(getOptInt(StyleEntity.RADIUS));
        }

        int radiusColor = getOptColor(StyleEntity.RADIUSCOLOR, 0);
        if (radiusColor != 0) {
            drawable.setColor(radiusColor);
        }
        setOptTextStyle(tv_title);

        if (TextUtils.isEmpty(getOptString(Params.TEXT)) && footerView != null) {
            tv_title.setVisibility(GONE);
            footerView.setMinimumHeight(radius);
        }else{
            if(tv_title != null)
                tv_title.setVisibility(VISIBLE);
        }
    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);

        if (tv_title != null) {
            tv_title.setText(getOptString(Params.TEXT));
        }
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }

    private void setRadius(int radius) {
        this.radius  = radius;
        drawable.setCornerRadius(dpToPx(radius));
        footerView.setBackground(drawable);
    }

    private void setRadius(JSONArray radiusJsonArray) {
        if (radiusJsonArray != null && footerView != null) {
            switch (radiusJsonArray.length()) {
                case 1:
                    try {
                        radius = radiusJsonArray.getInt(0);
                        drawable.setCornerRadius(dpToPx(radius));
                    } catch (JSONException e) {
                    }
                    break;
                case 2:
                    try {
                        int radiusTop = dpToPx(radiusJsonArray.getInt(0));
                        int radiusBottom = dpToPx(radiusJsonArray.getInt(1));
                        radius = radiusBottom;
                        float radii[] = new float[]{radiusTop, radiusTop, radiusTop, radiusTop,
                                radiusBottom, radiusBottom, radiusBottom, radiusBottom};
                        drawable.setCornerRadii(radii);
                    } catch (JSONException e) {
                    }
                    break;
                case 4:
                    try {
                        int radiusLeftTop = dpToPx(radiusJsonArray.getInt(0));
                        int radiusLeftBottom = dpToPx(radiusJsonArray.getInt(1));
                        int radiusRightTop = dpToPx(radiusJsonArray.getInt(2));
                        int radiusRightBottom = dpToPx(radiusJsonArray.getInt(3));
                        radius = radiusLeftBottom;
                        float radii4[] = new float[]{radiusLeftTop, radiusLeftTop, radiusRightTop, radiusRightTop,
                                radiusLeftBottom, radiusLeftBottom, radiusRightBottom, radiusRightBottom};
                        drawable.setCornerRadii(radii4);
                    } catch (JSONException e) {
                    }
                    break;
            }
        }
        footerView.setBackground(drawable);
    }
}
