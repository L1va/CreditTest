/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.l1va.credittest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l1va.credittest.utils.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabsFragment extends Fragment {

    static class TabPagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;

        TabPagerItem(CharSequence title, int indicatorColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
        }

        Fragment createFragment() {
            return GridFragment.newInstance(mTitle, mIndicatorColor);
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

    }

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mTabViewPager;

    private List<TabPagerItem> mTabs = new ArrayList<TabPagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new TabPagerItem(getString(R.string.tab_gallery), Color.BLUE));

        mTabs.add(new TabPagerItem(getString(R.string.tab_photos), Color.RED));

        mTabs.add(new TabPagerItem(getString(R.string.tab_cache), Color.YELLOW));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTabViewPager = (ViewPager) view.findViewById(R.id.tab_viewpager);
        mTabViewPager.setAdapter(new TabsFragmentPagerAdapter(getChildFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mTabViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return Color.GRAY;
            }

        });
    }

    class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

        TabsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }

    }

}