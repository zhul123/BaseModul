package com.component.base.fragment;

import android.view.ViewGroup;


import java.lang.ref.WeakReference;

import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author: wenzhihao769
 * @time: 2020/10/23
 * @description:
 * 为了解决使用 com.pah.view.stmarttablayout.utils.v4.FragmentPagerItemAdapter，出现白屏的问题
 * 如果vp不设置offsetLimit值，并且vp的Fragment onHiddenChange()方法不需要关注，可以使用此Adapter
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentPagerItems pages = null;
    private SparseArrayCompat<WeakReference<Fragment>> holder = null;

    public CommonFragmentPagerAdapter(FragmentManager fm, FragmentPagerItems pages) {
        super(fm);
        this.pages = pages;
        this.holder = new SparseArrayCompat<>(pages.size());
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Fragment getItem(int position) {
        return getPagerItem(position).instantiate(pages.getContext(), position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<Fragment>((Fragment) item));
        }
        return item;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return getPagerItem(position).getTitle();
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    public Fragment getPage(int position) {
        final WeakReference<Fragment> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

    public FragmentPagerItem getPagerItem(int position) {
        return pages.get(position);
    }


}
