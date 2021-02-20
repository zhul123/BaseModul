package com.blocks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blocks.clicksupport.CustomClickSupport;
import com.blocks.clicksupport.MyCardSupport;
import com.blocks.views.floorcells.LoginCell;
import com.blocks.views.floorcells.TextCell;
import com.blocks.views.floorviews.ButtonFloorView;
import com.blocks.views.floorviews.CheckBoxTextFloorView;
import com.blocks.views.floorviews.DividerFloorView;
import com.blocks.views.floorviews.EditFloorView;
import com.blocks.views.floorviews.FooterFloorView;
import com.blocks.views.floorviews.HeaderFloorView;
import com.blocks.views.floorviews.ImageFloorView;
import com.blocks.views.floorviews.ImageTextFloorView;
import com.blocks.views.floorviews.TitleBarFloorView;
import com.component.providers.common.BlocksProvider;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.core.adapter.GroupBasicAdapter;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.support.CardSupport;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

@Route(path = "/floor/tangramtest",name = "七巧板测试页面")
public class TangramTestActivity extends Activity implements BlocksProvider {
    TangramEngine engine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tangremw);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(this);
        builder.registerCell("InterfaceCell", CustomInterfaceView.class);
        builder.registerCell("AnnotationCell", CustomAnnotationView.class);
        builder.registerCell("Zhulei", ImageTextFloorView.class);
        builder.registerCell("header", HeaderFloorView.class);
        builder.registerCell("footer", FooterFloorView.class);
        builder.registerCell("divider", DividerFloorView.class);
        builder.registerCell("imageFloor", ImageFloorView.class);
        builder.registerCell("title", TitleBarFloorView.class);
        builder.registerCell("edit", EditFloorView.class);
        builder.registerCell("button", ButtonFloorView.class);
        builder.registerCell("text", TextCell.class,TextView.class);
        builder.registerCell("loginButton", LoginCell.class,ButtonFloorView.class);
        builder.registerCell("checkText", CheckBoxTextFloorView.class);
//        builder.registerCell("CustomCell", CustomCell.class, CustomCellView.class);
//        builder.registerCell("type", TestCell.class, TestView.class);
        recyclerView.setTag("blocksView");
        engine = builder.build();
        engine.addSimpleClickSupport(new CustomClickSupport());
        engine.register(CardSupport.class, new MyCardSupport());
        engine.bindView(recyclerView);
        byte[] bytes = Utils.getAssetsFile(this, "login.json");
        if (bytes != null) {
            String json = new String(bytes);
            try {
                JSONArray d = new JSONArray(json);
                engine.setData(d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        List listt = ((GroupBasicAdapter)recyclerView.getAdapter()).getGroups();
        List list = getParentCards();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(engine != null){
            engine.destroy();
        }
    }


    @Override
    public void init(Context context) {

    }

    @Override
    public List<Card> getParentCards() {
        try {
            return engine.getGroupBasicAdapter().getGroups();
        }catch (Exception e){
            return null;
        }
    }
}
