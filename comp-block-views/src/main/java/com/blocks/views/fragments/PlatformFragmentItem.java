package com.blocks.views.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.utils.JsonUtils;
import com.block.transform.platform.PlatformBean;
import com.block.transform.platform.PlatformTansform;
import com.blocks.views.R;
import com.blocks.views.floorcells.ApproveCell;
import com.blocks.views.floorcells.LoginCell;
import com.blocks.views.floorcells.LoginOutCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.floorviews.EditFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TitleBarFloorView;
import com.lib.block.style.ViewType;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class PlatformFragmentItem extends Fragment {
    public static final String DATAS = "datas";
    public static final String TYPE = "platformType";
    private RecyclerView mRecyclerView;
    private TangramEngine engine;
    private List<PlatformBean> datasList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = new NestRecyclerView(getContext());
        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDatas();
    }

    private void initDatas() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            String type = bundle.getString(TYPE);
            if("history".equals(type)){
            }
        }
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(getContext());
        builder.registerCell("imageView", ImageFloorView.class);
        builder.registerCell(ViewType.IMAGETEXTVIEW, ApproveCell.class, ImageTextFloorView.class);
        builder.registerCell("title", TitleBarFloorView.class);
        builder.registerCell("edit", EditFloorView.class);
        builder.registerCell("button", ButtonFloorView.class);
        builder.registerCell("text", TextCell.class, TextView.class);
        builder.registerCell("textView", TextCell.class, TextView.class);
        builder.registerCell("loginButton", LoginCell.class, ButtonFloorView.class);
        builder.registerCell("loginOutButton", LoginOutCell.class, ButtonFloorView.class);
        mRecyclerView.setTag("blocksView");
        engine = builder.build();
//        engine.addSimpleClickSupport(new CustomClickSupport());
        engine.bindView(mRecyclerView);
        if(datasList != null) {
            try {
                JSONArray datasJson = JsonUtils.getInstance().transFastJsonArrayToJsonArray(PlatformTansform.getInstance().transform(datasList));
                engine.setData(datasJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       /* byte[] bytes = getAssetsFile(getContext(), "cardstyle/platform/platformhead.json");
        if (bytes != null) {
            String json = new String(bytes);
            try {
                JSONArray d = new JSONArray(json);
                engine.setData(d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
    }

    public static byte[] getAssetsFile(Context context, String fileName) {
        InputStream inputStream;
        AssetManager assetManager = context.getAssets();
        try {
            inputStream = assetManager.open(fileName);

            BufferedInputStream bis = null;
            int length;
            try {
                bis = new BufferedInputStream(inputStream);
                length = bis.available();
                byte[] data = new byte[length];
                bis.read(data);

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
