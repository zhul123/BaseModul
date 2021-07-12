package com.blocks.views.floorcells;

import android.view.View;
import android.widget.TextView;

import com.base.utils.ARouterUtils;
import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.utils.ParamsUtils;
import com.blocks.views.utils.style.StyleUtils;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;

import androidx.annotation.NonNull;

public class TextCell extends BaseFloorCell<TextView> implements BaseFloorCell.OnChildClickListener {

    @Override
    public void postBindView(@NonNull TextView view) {
        super.postBindView(view);
        view.setText(getOptString(Params.TEXT));
        StyleUtils.getInstance().setTextViewStyle(view,this);
        setOnHolderChildClickListener(this,view);
    }

    @Override
    public void onChildClick(View view, BaseCell cell) {
        ARouterUtils.goNext(getOptString(Params.AROUTERURL));
    }
}
