package com.blocks.views.floorviews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.base.utils.ScreenUtils;
import com.blocks.pages.platform.PlatformAddPage;
import com.blocks.views.fragments.PlatformFragmentItem;
import com.component.base.fragment.CommonFragmentPagerAdapter;
import com.component.base.fragment.FragmentPagerItem;
import com.component.base.fragment.FragmentPagerItems;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.UltraViewPagerAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

public class TabViewPagerFloorView extends LinearLayout {
    private TabBarFloorView mTabBarFloorView;
    private PlatformFragmentItem.FunctionType mFunctionType;

    public TabViewPagerFloorView(Context context) {
        super(context);
        init();
    }

    public TabViewPagerFloorView(Context context, PlatformFragmentItem.FunctionType functionType) {
        super(context);
        mFunctionType = functionType;
        init();
    }

    public TabViewPagerFloorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabViewPagerFloorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOrientation(VERTICAL);

        mTabBarFloorView = new TabBarFloorView(getContext());

        //绑定控件
        UltraViewPager ultraViewPager = new UltraViewPager(getContext());
        //设置滑动的方向
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        FragmentManager fragmentManager = null;
        if (getContext() instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        }

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());
        addFragment(creator,"history","最近使用");
        addFragment(creator,"00","办公服务");
        addFragment(creator,"01","生活服务");
        addFragment(creator,"02","社区服务");
        CommonFragmentPagerAdapter commonFragmentPagerAdapter = new CommonFragmentPagerAdapter(fragmentManager, creator.create());
        //UltraPagerAdapter 绑定子view到UltraViewPager
        PagerAdapter adapter = new UltraViewPagerAdapter(commonFragmentPagerAdapter);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setScrollContainer(true);
        ultraViewPager.setVerticalScrollBarEnabled(true);
        mTabBarFloorView.setViewPager(ultraViewPager);

        addView(mTabBarFloorView);
        addView(ultraViewPager);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int recycleHeight = ScreenUtils.getScreenBounds(getContext())[1] / 3 * 2;
        params1.height = recycleHeight;
        setLayoutParams(params1);
    }

    private void addFragment(FragmentPagerItems.Creator creator,String appType,String title){
        Bundle bundle = new Bundle();
        bundle.putString(PlatformFragmentItem.APPTYPE, appType);
        if(mFunctionType != null) {
            bundle.putSerializable(PlatformFragmentItem.FUNCTIONTYPE, mFunctionType);
        }
        creator.add(title, PlatformFragmentItem.class, bundle);
    }

    /**
     * 默认是填充Match_parent 如果设置match则根据父布局中设置blockView标签的布局高度
     *
     */
    public void setHeightMatch() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }

}
