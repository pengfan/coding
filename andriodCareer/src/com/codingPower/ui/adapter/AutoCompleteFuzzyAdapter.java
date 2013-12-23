package com.codingPower.ui.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class AutoCompleteFuzzyAdapter<T> extends ArrayAdapter<T> implements
		Filterable {

	private ArrayList<T> mOriginalValues;
	private List<T> mObjects;
	private CustomFilter mFilter;
	private final Object mLock = new Object();

	public AutoCompleteFuzzyAdapter(Context context, int textViewResourceId,
			T[] objects) {
		super(context, textViewResourceId, objects);
		mObjects = Arrays.asList(objects);
	}

	public AutoCompleteFuzzyAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		mObjects = objects;
	}
	
	public AutoCompleteFuzzyAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, textViewResourceId, objects);
		mObjects = objects;
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
