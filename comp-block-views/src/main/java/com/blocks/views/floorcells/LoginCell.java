package com.blocks.views.floorcells;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.http.MyHttpUtils;
import com.base.utils.ARouterUtils;
import com.base.utils.savedata.sp.AppSharedPreferencesHelper;
import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.utils.CheckMustException;
import com.blocks.views.utils.FloorUtils;
import com.blocks.views.utils.ParamsUtils;
import com.base.utils.ToastUtil;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.core.adapter.GroupBasicAdapter;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoginCell extends BaseFloorCell<ButtonFloorView> {
    @Override
    public void postBindView(@NonNull ButtonFloorView view) {
        super.postBindView(view);
        view.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FloorUtils.isFastDoubleClick(1000))
                    return;
                try {
                    RecyclerView recyclerView = ((Activity) (v.getContext())).getWindow().getDecorView().findViewWithTag("blocksView");
                    List cards = ((GroupBasicAdapter)recyclerView.getAdapter()).getGroups();
                    if(cards != null) {
                        request(ParamsUtils.getInstance().complexAndCheckParams(cards));
                    }
                }catch (Exception e){
                    if(e instanceof CheckMustException){
                        Toast.makeText(view.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void request(Map params){
        MyHttpUtils.post(getOptString(Params.URL), params, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                ToastUtil.getInstance().makeText(result.toString());
                AppSharedPreferencesHelper.setToken(jsonObject.getString("token"));
                AppSharedPreferencesHelper.setUserInfo(jsonObject.getJSONObject("userInfo"));
                if(mContext != null && mContext instanceof Activity){
                    ((Activity)mContext).finish();
                }
                String url = getOptString(Params.AROUTERURL);
                ARouterUtils.goNext(getOptString(Params.AROUTERURL));
            }

            @Override
            public void onFail(String errMsg) {
                ToastUtil.getInstance().makeText(errMsg);
            }
        });
    }
}
