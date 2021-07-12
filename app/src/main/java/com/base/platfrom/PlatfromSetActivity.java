package com.base.platfrom;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.R;
import com.base.widget.dialog.hint.SweetAlertDialog;
import com.blocks.pages.platform.PlatformSetPage;
import com.component.base.BaseActivity;
import com.component.providers.app.AppProvider;
import com.component.providers.common.CommonProvider;

@Route(path = AppProvider.Path.PATH_PLATFORM_SET, name = "应用管理")
public class PlatfromSetActivity extends BaseActivity{
    @Autowired(name = "url", desc = "加载本地数据地址")
    protected String dataUrl = "";
    @Autowired(name = CommonProvider.CommonConstantDef.TITLE, desc = "标题")
    protected String title = "";
    private  TextView mTextView;
    private  TextView mTextViewMore;
    private ImageView mImageViewMore;
    private PlatformSetPage mPlatformSetPage;
    private SweetAlertDialog mDialog;


    @Override
    protected int getlayoutId() {
        return R.layout.floor_activity_tangrem_platform_set;
    }

    @Override
    public void initView() {
        super.initView();
        mTextView = findViewById(R.id.tv_title);
        mPlatformSetPage = findViewById(R.id.v_setPage);
        mTextViewMore = findViewById(R.id.tv_more);
        mTextViewMore.setOnClickListener(this);

    }

    @Override
    public void initData() {
        super.initData();
        mTextView.setText("工作台设置");
        mTextViewMore.setText("完成");
//        mImageViewMore.setVisibility(View.VISIBLE);
//        mImageViewMore.setImageResource(R.drawable.icon_save);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_more) {
            mPlatformSetPage.save();
        }else if(v.getId() == R.id.iv_back){
            if(!mPlatformSetPage.haveChange()){
                super.onClick(v);
                return;
            }
            if(mDialog == null) {
                mDialog = new SweetAlertDialog(this)
                        .setContentText("已修改，是否放弃修改？")
                        .setCancelText("再想想")
                        .setCallBack(new SweetAlertDialog.AlertCallBack() {
                            @Override
                            public void onCancelClick(SweetAlertDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void oncinfirmClick(SweetAlertDialog dialog) {
                                dialog.dismiss();
                                PlatfromSetActivity.super.onClick(v);
                            }
                        });
            }
            mDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
