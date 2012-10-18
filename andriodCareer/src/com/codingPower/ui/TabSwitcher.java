package com.codingPower.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import com.codingPower.R;
import com.codingPower.ui.tab.FormFragment;
import com.codingPower.ui.tab.MyListFragment;
import com.codingPower.ui.tab.NormalFragment;
/**
 * viewPager sample
 * @author pengfan
 *
 */
public class TabSwitcher extends FragmentActivity
{
 	private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.view_page);
	   viewPager = (ViewPager)findViewById(R.id.pager);
	   
	   viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), 3));
	   viewPager.setOffscreenPageLimit(2);
	}
	
	private class TabAdapter extends FragmentStatePagerAdapter
	{
		private int size;
		
		public TabAdapter(FragmentManager fm, int size)
		{
			super(fm);
			this.size = size;
		}

		@Override
		public Fragment getItem(int pos)
		{
			switch(pos)
			{
				case 0:
				{
					return new MyListFragment();
				}
				case 1:
				{
					return new FormFragment();
				}
				case 2:
				{
					return new NormalFragment();
				}
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return size;
		}
		
	}
}
