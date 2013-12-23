package com.codingPower.ui.adapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class HighlightAutoCompleteFuzzyAdapter<T> extends BaseAdapter implements
		Filterable {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mTextViewResourceId;
	
	private AutoCompleteTextView mAutoCompleteView;
	private CustomFilter mFilter;

	private List<T> mOriginalValues;
	private List<T> mObjects;
	private final Object mLock = new Object();
	private String mColorStr = "#F3822A";


	public HighlightAutoCompleteFuzzyAdapter(AutoCompleteTextView autoCompleteTextView,
			int textViewResourceId, T[] objects) {
		init(autoCompleteTextView, textViewResourceId);
		mObjects = Arrays.asList(objects);
	}
	
	public HighlightAutoCompleteFuzzyAdapter(AutoCompleteTextView autoCompleteTextView,
			int textViewResourceId, List<T> objects) {
		init(autoCompleteTextView, textViewResourceId);
		mObjects = objects;
	}
	
	private void init(AutoCompleteTextView autoCompleteTextView,
			int textViewResourceId){
		mAutoCompleteView = autoCompleteTextView;
		mContext = mAutoCompleteView.getContext();
		mInflater  = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTextViewResourceId = textViewResourceId;
	}
	
	public HighlightAutoCompleteFuzzyAdapter<T> color(int colorResId){
		int color = mContext.getResources().getColor(colorResId);
		StringBuffer colorRes = new StringBuffer("#");
		appendHexString(Color.alpha(color) % 255, colorRes);
		appendHexString(Color.red(color), colorRes);
		appendHexString(Color.green(color), colorRes);
		appendHexString(Color.blue(color), colorRes);
		mColorStr = colorRes.toString();
		
		Log.i("Color", Color.alpha(color) + "," +mColorStr);
		return this;
	}
	
	private void appendHexString(int val, StringBuffer sb){
		String color = Integer.toHexString(val);
		if(color.length() == 1){
			sb.append("0");
		}
		sb.append(color);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(mTextViewResourceId, parent, false);
		}
		TextView titleView = (TextView)convertView;
		String v = String.valueOf(mObjects.get(position));
		String hightLightStr = mAutoCompleteView.getText().toString().trim();
		if(hightLightStr.isEmpty()){
			titleView.setText(v);
		}
		else{
			v = v.replaceAll("(?i)(" + hightLightStr +")", "<font color='" + mColorStr +"'>$1</font>");
			titleView.setText(Html.fromHtml(v));
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new CustomFilter();
		}
		return mFilter;
	}

	public int getCount() {
		return mObjects.size();
	}

	public T getItem(int position) {
		return mObjects.get(position);
	}

	private class CustomFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<T>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<T> list;
				synchronized (mLock) {
					list = new ArrayList<T>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				ArrayList<T> values;
				synchronized (mLock) {
					values = new ArrayList<T>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<T> newValues = new ArrayList<T>();

				for (int i = 0; i < count; i++) {
					final T value = values.get(i);
					final String valueText = value.toString().toLowerCase();

					if (valueText.contains(prefixString)) {
						newValues.add(value);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			//noinspection unchecked
			mObjects = (List<T>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

}
