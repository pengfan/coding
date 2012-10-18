package com.codingPower.ui.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.codingPower.R;

/**
 * tab切换第一个页面，是包含列表的页面
 * @author pengfan
 *
 */
public class MyListFragment extends Fragment
{

	private ListView listView;
	private String[] data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		final View v = inflater.inflate(R.layout.list_fragment, container, false);
		listView = (ListView)v.findViewById(R.id.listView);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		data = new String[1000];
		for(int i = 0; i< 1000; i++)
		{
			data[i] = "item" + i;
		}
		
		BaseAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
		listView.setAdapter(adapter);
	}

	
}
