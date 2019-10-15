package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private List<Fragment> fragments = new ArrayList<>();

    public SectionsPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mContext = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        if(fragments == null || fragments.isEmpty()){
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return fragments.size();
    }
}