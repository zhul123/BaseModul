package com.base.widget.popwindows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.capinfo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PopMenuWindow{
    static PopMenuWindow instance = null;
    private View contentView;
    private List<PopWinMenuBean> mDatas;
    private CustomPopWindow mMenuPopWindow;
    private PopListAdapter mAdapter;
    private PopListAdapter.OnPopMenuClick mOnPopMenuClick;
    public static PopMenuWindow getInstance(){
        if (instance == null){
            synchronized (PopMenuWindow.class){
                if (instance == null){
                    instance = new PopMenuWindow();
                }
            }
        }
        return instance;
    }


    public CustomPopWindow getPopWindow(Context context){
//        if(mMenuPopWindow == null){
            contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_list,null);
            //处理popWindow 显示内容
            handleListView(contentView);
            //创建并显示popWindow
        mMenuPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                    .setView(contentView)
                    .setFocusable(true)
                    .setOutsideTouchable(true)
                    .setAnimationStyle(R.style.CustomPopWindowStyle)
                    .create();
//        }
        mMenuPopWindow.setContext(context);
        return mMenuPopWindow;
    }

    public PopMenuWindow setDatas(List<PopWinMenuBean> datas){
        this.mDatas = datas;
        if(mAdapter != null){
            mAdapter.setData(datas);
        }
        return this;
    }

    public PopMenuWindow setOnPopMenuClick(PopListAdapter.OnPopMenuClick onPopMenuClick){
        this.mOnPopMenuClick = onPopMenuClick;
        if(mAdapter != null){
            mAdapter.setOnPopMenuClick(onPopMenuClick);
        }
        return this;
    }

    private void handleListView(View contentView){
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
//        recyclerView.addItemDecoration(new DividerItemDecoration(contentView.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(contentView.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new PopListAdapter();
        mAdapter.setData(mDatas);
        mAdapter.setOnPopMenuClick(mOnPopMenuClick);
        recyclerView.setAdapter(mAdapter);
    }
}

