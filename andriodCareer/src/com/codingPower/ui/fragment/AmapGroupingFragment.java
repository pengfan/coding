package com.codingPower.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.codingPower.R;
import com.codingPower.ui.amap.AMapFragment;
import com.codingPower.ui.amap.MarkViewAdapter;
import com.codingPower.ui.amap.Markable;
import com.codingPower.ui.amap.MarkableGroup;

public class AmapGroupingFragment extends AMapFragment {
	private static LatLng defaultCenter = new LatLng(32.0223, 118.782);
	
	@Override
	protected View onCreateMapContainer(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.single_map, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setMarkGroupViewAdapter(new MarkViewAdapter() {
			@Override
			public View getView(LayoutInflater inflater, Markable mark) {
				View view = inflater.inflate(R.layout.group_mark, null);
				((TextView) view.findViewById(R.id.groupCount))
						.setText(((MarkableGroup) mark).getMarkSize() + "");
				return view;
			}
		});
	}
	
	@Override
	public void onMapLoaded() {
		super.onMapLoaded();
		removeAll();
		initData();
	}
	
	private void initData(){
		Random random = new Random();
		LatLng lastLoc = defaultCenter.clone();
		List<MyTestLoc> res = new ArrayList<MyTestLoc>();
		for(int i = 0 ;  i < 2000; i++){
			LatLng cLoc = new LatLng(lastLoc.latitude + judge(random) * random.nextDouble() / 1E3
					,lastLoc.longitude + judge(random) * random.nextDouble() / 1E3);
			res.add(new MyTestLoc(i + "" , cLoc));
			lastLoc = cLoc;
		}
		groupingBatch(res, true);
	}

	private int judge(Random random){
		if(random.nextBoolean())
			return 1;
		return -1;
	}
	
	private static class MyTestLoc extends Markable{
		private String id;
		private LatLng pos;
		
		private MyTestLoc(String id, LatLng pos){
			this.id = id;
			this.pos = pos;
		}
		
		@Override
		public String ID() {
			return id;
		}

		@Override
		public int iconRes() {
			return R.drawable.point;
		}

		@Override
		public LatLng position() {
			return pos;
		}
		
	}
	
}
