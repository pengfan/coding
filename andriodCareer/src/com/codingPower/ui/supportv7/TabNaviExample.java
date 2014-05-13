package com.codingPower.ui.supportv7;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingPower.R;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 菜单式导航
 * 
 * 自定义控件生成的 contextMenu
 * 
 * @author pengf
 * 
 */
public class TabNaviExample extends ActionBarActivity {

	private View mActionBarFlag;
	private static final String[] CONTENT = new String[] { "Recent", "Artists",
			"Albums", "Songs", "Playlists", "Genres" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_supportv7_tabnavi);
		
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
						| ActionBar.DISPLAY_SHOW_CUSTOM);

		mActionBarFlag = getLayoutInflater().inflate(R.layout.action_bar_flag,
				null);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.LEFT;
		loadInfo();
		getSupportActionBar().setCustomView(mActionBarFlag, lp);

		FragmentPagerAdapter adapter = new GoogleMusicAdapter(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	private void loadInfo() {
		((ImageView) mActionBarFlag.findViewById(R.id.avatorView))
				.setImageResource(R.drawable.cycle);
		((TextView) mActionBarFlag.findViewById(R.id.nameView))
				.setText("smily");
	}

	class GoogleMusicAdapter extends FragmentPagerAdapter {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return SimpleFragment.createBy(CONTENT[position % CONTENT.length]);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
}
