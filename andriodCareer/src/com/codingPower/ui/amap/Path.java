package com.codingPower.ui.amap;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;

public class Path {
	private String mName;
	private List<LatLng> mPointList = new ArrayList<LatLng>();
	private int mColor = android.R.color.darker_gray;
	private Polyline mPolyline;
	private int startBitmapRes, endBitmapRes;
	private Flag startMark, endMark;
	
	public Path(String name) {
		mName = name;
	}

	public void addPoint(LatLng point){
		mPointList.add(point);
		if(mPolyline != null){
			mPolyline.setPoints(mPointList);
			if(endMark != null){
				endMark.setPosition(point);
			}
		}
	}
	
	public void setStartBitmapRes(int startBitmapRes) {
		this.startBitmapRes = startBitmapRes;
	}

	public void setEndBitmapRes(int endBitmapRes) {
		this.endBitmapRes = endBitmapRes;
	}

	public Markable getStartMark(){
		if(startMark != null) return startMark;
		else if(startBitmapRes > 0 && !mPointList.isEmpty()){
			 startMark = new Flag("start", startBitmapRes, mPointList.get(0));
			 return startMark;
		}
		return null;
	}
	
	public Markable getEndMark(){
		LatLng endPoint = mPointList.get(mPointList.size() - 1);
		if(endMark != null) {
			endMark.setPosition(endPoint);
			return endMark;
		}
		else if(endBitmapRes > 0 && !mPointList.isEmpty()){
			endMark = new Flag("end", endBitmapRes, endPoint);
			return endMark;
		}
		return null;
	}
	
	public void setColor(int color){
		mColor = color;
	}
	
	public List<LatLng> getPointList(){
		return mPointList;
	}
	
	public String getName(){
		return mName;
	}
	
	public int getColor(){
		return mColor;
	}
	
	public void setPolyLine(Polyline line){
		mPolyline = line;
	}
	
	public Polyline getPolyline(){
		return mPolyline;
	}
	
	private static class Flag extends Markable{
		private int iconRes;
		private LatLng latLng;
		private String id;
		
		public Flag(String id, int iconRes, LatLng latLng) {
			this.iconRes = iconRes;
			this.latLng = latLng;
			this.id = id;
		}

		@Override
		public String ID() {
			return id;
		}

		@Override
		public int iconRes() {
			return iconRes;
		}

		@Override
		public LatLng position() {
			return latLng;
		}
		
		public void setPosition(LatLng latLng){
			this.latLng = latLng;
			if(getMarker() != null)
				getMarker().setPosition(latLng);
		}
		
	}
}
