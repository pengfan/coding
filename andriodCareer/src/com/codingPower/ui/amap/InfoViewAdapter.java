package com.codingPower.ui.amap;

import android.view.View;

import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.model.Marker;

public abstract class InfoViewAdapter implements OnInfoWindowClickListener, InfoWindowAdapter{

	@Override
	public void onInfoWindowClick(Marker marker) {
		infoClick((Markable)marker.getObject());
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View view = infoContents((Markable)marker.getObject());
		return view;
	}
	
	public abstract View infoContents(Markable markable);
	
	public abstract void infoClick(Markable markable);
	
}
