package com.blocks.views.floorcells;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.http.MyHttpUtils;
import com.base.imagehelper.ImageHelper;
import com.base.utils.ARouterUtils;
import com.base.utils.ToastUtil;
import com.base.utils.savedata.sp.AppSharedPreferencesHelper;
import com.blocks.views.R;
import com.blocks.views.base.BaseFloorCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.utils.CheckMustException;
import com.blocks.views.utils.FloorUtils;
import com.blocks.views.utils.ParamsUtils;
import com.blocks.views.utils.style.StyleUtils;
import com.lib.block.entity.base.StyleEntity;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.core.adapter.GroupBasicAdapter;

import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ApproveCell extends BaseFloorCell<ImageTextFloorView> {

    private ImageView iv_img;
    private TextView tv_title;
    @Override
    public void postBindView(@NonNull ImageTextFloorView floorView) {
        super.postBindView(floorView);
        iv_img = floorView.findViewById(R.id.iv_img);
        tv_title = floorView.findViewById(R.id.tv_title);
        ImageHelper.getInstance().setRadiusDrawable(iv_img, getOptString(Params.IMGURL), getOptInt(StyleEntity.RADIUS));
        tv_title.setText(getOptString(Params.TEXT));
        StyleUtils.getInstance().setTextViewStyle(tv_title,this);
        setImgLayoutParams(iv_img);
    }
}
