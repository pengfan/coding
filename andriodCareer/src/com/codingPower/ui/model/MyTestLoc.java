package com.codingPower.ui.model;
import com.amap.api.maps.model.LatLng;
import com.codingPower.R;
import com.codingPower.ui.amap.Markable;

public  class MyTestLoc extends Markable{
		private String id;
		private LatLng pos;
		
		public MyTestLoc(String id, LatLng pos){
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