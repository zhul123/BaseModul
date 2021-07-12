package com.blocks.views.floorviews;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.utils.ScreenUtils;
import com.blocks.views.fragments.PlatformFragmentItem;
import com.component.base.fragment.CommonFragmentPagerAdapter;
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
    private RecyclerView parentView;

    public TabViewPagerFloorView(Context context) {
        super(context);
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
        Bundle bundle = new Bundle();
        bundle.putString(PlatformFragmentItem.TYPE,"history");
        creator.add("最近使用", PlatformFragmentItem.class,bundle);
        bundle.putString(PlatformFragmentItem.TYPE,"00");
        creator.add("办公服务", PlatformFragmentItem.class,bundle);
        bundle.putString(PlatformFragmentItem.TYPE,"01");
        creator.add("生活服务", PlatformFragmentItem.class,bundle);
        bundle.putString(PlatformFragmentItem.TYPE,"02");
        creator.add("社区服务", PlatformFragmentItem.class,bundle);

        CommonFragmentPagerAdapter commonFragmentPagerAdapter = new CommonFragmentPagerAdapter(fragmentManager, creator.create());
        //UltraPagerAdapter 绑定子view到UltraViewPager
        PagerAdapter adapter = new UltraViewPagerAdapter(commonFragmentPagerAdapter);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setScrollContainer(true);
        ultraViewPager.setVerticalScrollBarEnabled(true);
        mTabBarFloorView.setViewPager(ultraViewPager);

        addView(mTabBarFloorView);
        addView(ultraViewPager);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int recycleHeight = ScreenUtils.getScreenBounds(getContext())[1] / 3 * 2;
        params1.height = recycleHeight;
        setLayoutParams(params1);
    }


}