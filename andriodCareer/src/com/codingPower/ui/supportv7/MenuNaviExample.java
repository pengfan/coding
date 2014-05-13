package com.codingPower.ui.supportv7;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codingPower.R;

/**
 * 菜单式导航
 * 
 * 自定义控件生成的 contextMenu
 * @author pengf
 *
 */
public class MenuNaviExample extends ActionBarActivity implements OnNavigationListener{

	private NaviAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME , ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		
		mAdapter = new NaviAdapter();
		getSupportActionBar().setListNavigationCallbacks(mAdapter, this);

	}
	
	private class NaviAdapter extends BaseAdapter{
		private String[] navis;
		
		private NaviAdapter(){
			navis = getResources().getStringArray(R.array.menu_navi_array);
		}
		
		@Override
		public int getCount() {
			return navis.length;
		}

		@Override
		public String getItem(int position) {
			return navis[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
				((TextView)convertView).setTextColor(getResources().getColor(android.R.color.white));
			}
			TextView titleView = (TextView)convertView;
			titleView.setText(navis[position]);
			return convertView;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {
		Toast.makeText(this, mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
		return true;
	}
}
