package com.blocks.views.floorviews;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.base.http.MyHttpUtils;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.CheckMustException;
import com.blocks.views.utils.FloorUtils;
import com.blocks.views.utils.ParamsUtils;
import com.blocks.views.utils.style.StyleUtils;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.core.adapter.GroupBasicAdapter;
import com.tmall.wireless.tangram.structure.BaseCell;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public class ButtonFloorView extends BaseFloorView {
    public Button mButton;
    public ButtonFloorView(Context context) {
        super(context);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void init() {
        mButton = new Button(mContext);
        addView(mButton,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        mButton.setText(getOptString(Params.TEXT));
        StyleUtils.getInstance().setButtonStyle(mButton,mBaseCell);
    }

    @Override
    public void setLsn() {
        super.setLsn();
        this.getParent();
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FloorUtils.isFastDoubleClick(1000)) {
                    print("isFastDoubleClick");
                    return;
                }else{
                    print("isFastDoubleClick11111");
                }
                try {
                    RecyclerView recyclerView = ((Activity) (v.getContext())).getWindow().getDecorView().findViewWithTag("blocksView");
                    List cards = ((GroupBasicAdapter)recyclerView.getAdapter()).getGroups();
                    if(cards != null) {
                        request(ParamsUtils.getInstance().complexAndCheckParams(cards));
                    }
                }catch (Exception e){
                    if(e instanceof CheckMustException){
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void request(Map params){
        MyHttpUtils.post(getOptString(Params.URL), params, new MyHttpUtils.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(mContext,result.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String errMsg) {
                Toast.makeText(mContext,errMsg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void setCustomStyle() {

    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
