package com.blocks.views.floorcells;

import android.view.View;

import com.base.utils.ARouterUtils;
import com.base.utils.savedata.sp.AppSharedPreferencesHelper;
import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.utils.FloorUtils;
import com.lib.block.style.Params;

import androidx.annotation.NonNull;

public class LoginOutCell extends BaseFloorCell<ButtonFloorView> {
    @Override
    public void postBindView(@NonNull ButtonFloorView view) {
        super.postBindView(view);
        view.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FloorUtils.isFastDoubleClick(1000))
                    return;
                AppSharedPreferencesHelper.setToken(null);
                AppSharedPreferencesHelper.setUserInfo(null);

                ARouterUtils.goNext(getOptString(Params.AROUTERURL));
            }
        });
    }
}
