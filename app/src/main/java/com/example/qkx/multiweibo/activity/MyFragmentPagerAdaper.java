package com.example.qkx.multiweibo.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by qkx on 15/11/23.
 */
public class MyFragmentPagerAdaper extends FragmentPagerAdapter {
    List<Fragment> mList;


    public void setList(List<Fragment> mList) {
        this.mList = mList;
    }

    public MyFragmentPagerAdaper(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdaper(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
