/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codingPower.framework.ui.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * 为了更好的使用tab类型界面
 * 只需要在页面的button定义android:tag，并且把其注册进来
 * 设置android:onClick为tabChanged;
 * @author pengfan
 *
 */
public class Tabs extends SherlockFragmentActivity {
    TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void tabChanged(View view) {
        mTabManager.onTabChanged(view.getId());
    }

    protected void initTabManager(int containerId) {
        mTabManager = new TabManager(this, containerId);
    }

    protected void addTab(int tabViewId, Class<?> clss, Bundle _args) {
        mTabManager.addTab(tabViewId, clss, _args);
    }

    protected Fragment findTabFragment(int tabViewId) {
        return mTabManager.getTabFragment(tabViewId);
    }
    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager {
        private final FragmentActivity mActivity;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final int tabViewId;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(int _tabViewId, Class<?> _class, Bundle _args) {
                tabViewId = _tabViewId;
                clss = _class;
                args = _args;
            }
        }

        public TabManager(FragmentActivity activity, int containerId) {
            mActivity = activity;
            mContainerId = containerId;
        }

        public void addTab(int tabViewId, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(tabViewId, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(String.valueOf(tabViewId));
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(String.valueOf(tabViewId), info);
        }

        public Fragment getTabFragment(int tabViewId) {
            TabInfo tab = mTabs.get(String.valueOf(tabViewId));
            if (tab == null)
                return null;
            else
                return tab.fragment;
        }

        public void onTabChanged(int tabViewId) {
            TabInfo newTab = mTabs.get(String.valueOf(tabViewId));
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                        mActivity.findViewById(mLastTab.tabViewId).setSelected(false);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, String.valueOf(newTab.tabViewId));
                    } else {
                        ft.attach(newTab.fragment);
                    }
                    mActivity.findViewById(newTab.tabViewId).setSelected(true);
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
}
