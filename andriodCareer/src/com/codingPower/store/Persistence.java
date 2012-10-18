package com.codingPower.store;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codingPower.R;
import com.codingPower.store.model.Info;
/**
 * 持久层的展示类
 * @author pengfan
 *
 */
public class Persistence extends ListActivity
{
	private List<Info> data;
	private InfoListAdapter adapter;
	private InfoManager manager;
	private static String[] genderExpression;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		genderExpression = new String[2];
		genderExpression[0] = getResources().getString(R.string.persistence_info_male);
		genderExpression[1] = getResources().getString(R.string.persistence_info_female);
		manager = new InfoManager();
		manager.init(this);
		data = manager.query();
		adapter = new InfoListAdapter();
		setListAdapter(adapter);
		super.onCreate(savedInstanceState);
	}

	private class InfoListAdapter extends BaseAdapter
	{
		public InfoListAdapter()
		{
			
		}
		
		@Override
		public int getCount()
		{
			return data.size();
		}

		@Override
		public Object getItem(int pos)
		{
			return data.get(pos);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = LayoutInflater.from(Persistence.this).inflate(R.layout.info_item, null);
			}
			TextView name = (TextView)convertView.findViewById(R.id.name);
			TextView gender = (TextView)convertView.findViewById(R.id.gender);
			TextView birthday = (TextView)convertView.findViewById(R.id.birthday);
			TextView age = (TextView)convertView.findViewById(R.id.age);
			TextView description = (TextView)convertView.findViewById(R.id.description);
			
			name.setText(data.get(position).getName());
			gender.setText(data.get(position).isGender()? genderExpression[0]: genderExpression[1]);
			birthday.setText(data.get(position).getBirthdayExpression());
			age.setText(data.get(position).getAge() + "");
			description.setText(data.get(position).getDescription());
			
			return convertView;
		}
		
	}
}
