package com.blocks.clicksupport;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blocks.R;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.support.CardSupport;

import org.json.JSONException;
import org.json.JSONObject;

public class MyCardSupport extends CardSupport {
    private static final String TAG = "MyCardSupport";
    private static final String BGDRAWABLEBGCOLOR = "bgDrawableBgColor";
    private static final String BGDRAWABLERADIUS = "bgDrawableRadius";
    private int DEFAULTCOLOR = Color.WHITE;
    private GradientDrawable drawable;
    @Override
    public void onBindBackgroundView(View layoutView, Card card) {
        drawable = new GradientDrawable();
        try {
            JSONObject styleJson = card.extras.getJSONObject(Card.KEY_STYLE);
            drawable.setColor(getOptColor(styleJson,BGDRAWABLEBGCOLOR));
            int radius = Style.dp2px(styleJson.getInt(BGDRAWABLERADIUS));
            drawable.setCornerRadius(radius);
            layoutView.setBackground(drawable);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void onUnbindBackgroundView(View layoutView, Card card) {
        super.onUnbindBackgroundView(layoutView, card);
        layoutView.setBackground(null);
    }

    protected int getOptColor(JSONObject jsonObject, String key) {
        String colorStr = getOptString(jsonObject , key);
        if (TextUtils.isEmpty(colorStr)) {
            return DEFAULTCOLOR;
        } else {
            return Style.parseColor(colorStr, DEFAULTCOLOR);
        }
    }

    protected  String getOptString(JSONObject jsonObject, String key){
        if(jsonObject != null){
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                return "";
            }
        }else{
            return "";
        }
    }
}
