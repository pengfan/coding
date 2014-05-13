package com.codingPower.ui.supportv7;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingPower.R;

public class SimpleFragment extends Fragment {

	private TextView textView;
	private String showText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ui_supportv7_overlook_simple, null);
		textView = (TextView)view.findViewById(R.id.textView);
		textView.setText(showText);
		return view;
	}

	public static SimpleFragment createBy(String text){
		SimpleFragment sf = new SimpleFragment();
		sf.showText = text;
		return sf;
	}
}
