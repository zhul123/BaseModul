package com.blocks.views.floorviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blocks.views.R;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

/**
 * @Auter zhulei
 * 左边title,右边图片样式
 */
public class TitleBarFloorView extends BaseFloorView<LinearLayout.LayoutParams> {
    private int DEFALUTWAH = dpToPx(45);//默认图片宽高
    View view ;
    TextView tv_title;
    ImageView iv_more;
    private LinearLayout.LayoutParams mParams;

    public TitleBarFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.floor_title_more,null);
        addView(view,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv_title = findViewById(R.id.tv_title);
        iv_more = findViewById(R.id.iv_more);
        mParams = (LinearLayout.LayoutParams) iv_more.getLayoutParams();
    }

    @Override
    protected void setCustomStyle() {
        setOptImageStyle(iv_more);
        setOptTextStyle(tv_title);
    }


    @Override
    public void onItemClick() {

    }


    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        tv_title.setText(getOptString(Params.TEXT));
        loadImage(iv_more);
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
