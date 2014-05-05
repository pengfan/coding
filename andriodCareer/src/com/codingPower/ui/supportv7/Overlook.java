package com.codingPower.ui.supportv7;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codingPower.R;

public class Overlook extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private int checkIndex = -1;
	private List<FragmentTab> fragmentTabList = new ArrayList<FragmentTab>();
	private String[] naviArray;
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_supportv7_overlook);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		naviArray = getResources().getStringArray(R.array.navi_array);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.ui_supportv7_overlook_list_item, naviArray));
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selectItem(position);
			}
		});

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		init();

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void selectItem(int position) {
		switchTab(position);
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(fragmentTabList.get(position).title);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void switchTab(int index) {
		if (index != checkIndex) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (checkIndex >= 0) {
				Fragment lastFragment = fragmentTabList.get(checkIndex).mFragment;
				if (lastFragment != null && !lastFragment.isDetached()) {
					ft.detach(lastFragment);
				}
			}
			FragmentTab newTab = fragmentTabList.get(index);
			
			if (!newTab.isAdded) {
				ft.add(R.id.content_frame, newTab.mFragment);
			} else {
				ft.attach(newTab.mFragment);
			}
			newTab.isAdded = true;
			checkIndex = index;
			ft.commit();
		}
		
	}

	private void init() {
		FragmentTab tab = new FragmentTab();
		tab.title = naviArray[0];
		tab.mFragment = new SimpleFragment();
		fragmentTabList.add(tab);

		tab = new FragmentTab();
		tab.title = naviArray[1];
		tab.mFragment = new SearchFragment();
		fragmentTabList.add(tab);

		tab = new FragmentTab();
		tab.title = naviArray[2];
		tab.mFragment = new ShareFragment();
		fragmentTabList.add(tab);

		tab = new FragmentTab();
		tab.title = naviArray[3];
		tab.mFragment = new SimpleFragment();
		fragmentTabList.add(tab);

		tab = new FragmentTab();
		tab.title = naviArray[4];
		tab.mFragment = new SimpleFragment();
		fragmentTabList.add(tab);
	}

	private static class FragmentTab {
		Fragment mFragment;
		String title;
		boolean isAdded;
	}
}
