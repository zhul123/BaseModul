package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blocks.views.R;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @Auter zhulei
 * Header模板
 * radius：背景圆角
 * radiusBgColor：圆角控件背景颜色
 * text:标题
 * textSize 字体大小
 * textColor 字体颜色
 * textStyle 字体样式（italic 斜体，bold 加粗，nomal 默认）
 */
public class HeaderFloorView extends BaseFloorView {
    private TextView tv_title;
    private View headerView;
    private GradientDrawable drawable;
    private int radius;

    public HeaderFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        headerView = LayoutInflater.from(mContext).inflate(R.layout.floor_header, null);
        addView(headerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        drawable = new GradientDrawable();
        tv_title = findViewById(R.id.tv_title);
    }

    @Override
    protected void setCustomStyle() {
        //兼容radius为整数情况
        if (getOptJsonArray(ParamsUtils.RADIUS) != null) {
            setRadius(getOptJsonArray(ParamsUtils.RADIUS));
        } else {
            setRadius(getOptInt(ParamsUtils.RADIUS));
        }

        int radiusColor = getOptColor(ParamsUtils.RADIUSCOLOR,0);
        if (radiusColor != 0) {
            drawable.setColor(radiusColor);
        }else{
            drawable.setColor(Color.TRANSPARENT);
        }

        setOptTextStyle(tv_title);

        if(TextUtils.isEmpty(getOptString(ParamsUtils.TEXT)) && headerView != null){
            tv_title.setVisibility(GONE);
            headerView.setMinimumHeight(radius);
        }else{
            if(tv_title != null) {
                tv_title.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick() {
        Toast.makeText(mContext, "itemClick", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);

        if (tv_title != null) {
            tv_title.setText(getOptString(ParamsUtils.TEXT));
        }
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }

    private void setRadius(int radius) {
        this.radius  =  radius;
        drawable.setCornerRadius(dpToPx(radius));
        headerView.setBackground(drawable);
    }

    private void setRadius(JSONArray radiusJsonArray) {
        if (radiusJsonArray != null && headerView != null) {
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
                        radius = radiusTop;
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
                        radius = radiusLeftTop;
                        float radii4[] = new float[]{radiusLeftTop, radiusLeftTop, radiusRightTop, radiusRightTop,
                                radiusLeftBottom, radiusLeftBottom, radiusRightBottom, radiusRightBottom};
                        drawable.setCornerRadii(radii4);
                    } catch (JSONException e) {
                    }
                    break;
            }
        }
        headerView.setBackground(drawable);
    }
}
