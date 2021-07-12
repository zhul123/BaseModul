package com.blocks.views.floorviews;

import android.content.Context;

import com.blocks.views.base.BaseFloorView;
import com.tmall.wireless.tangram.structure.BaseCell;


/**
 * @Auter zhulei
 * 分割线模板模板
 * 必须设置height才能有高度
 * bgColor改变分割线颜色
 */
public class DividerFloorView extends BaseFloorView {

    public DividerFloorView(Context context) {
        super(context);
    }

    @Override
    public void init() {
    }

    @Override
    protected void setCustomStyle() {

    }


    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
