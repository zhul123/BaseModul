package com.xylink.sdk.sample.share.picture;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PicturePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> picturePaths = new ArrayList<>();

    public PicturePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public PictureFragment.OnPagerClickListener listener;

    public void setPicturePaths(List<String> picturePaths) {
        this.picturePaths = picturePaths;
    }

    @Override
    public Fragment getItem(int position) {
        PictureFragment fragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", picturePaths.get(position));
        fragment.setArguments(bundle);
        fragment.setOnPagerClickListener(listener);
        return fragment;
    }

    @Override
    public int getCount() {
        return picturePaths.size();
    }

    public void setOnPagerListener(PictureFragment.OnPagerClickListener listener) {
        this.listener = listener;
    }
}
