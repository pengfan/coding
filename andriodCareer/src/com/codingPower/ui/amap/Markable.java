package com.codingPower.ui.amap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

/**
 * 标记的父类
 * @author pengf
 *
 */
public abstract class Markable {
	
	public static int CENTER = 1;
	
	private Marker marker;
	//Id
	public abstract String ID();
	//显示的图标样式
	public abstract int iconRes();
	//经纬度
	public abstract LatLng position(); 
	public int direction(){
		return -1;
	}
	public int checkedIconRes(){
		return -1;
	}
	public String title(){
		return null;
	}
	private int gravity = -1;
	
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	public int getGravity() {
		return gravity;
	}
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}
	
	public BitmapDescriptor icon(Resources res){
		return BitmapDescriptorFactory.fromBitmap(transBitmap(res, iconRes()));
	}
	
	public BitmapDescriptor checkedIcon(Resources res){
		if(checkedIconRes() != -1)
			return BitmapDescriptorFactory.fromBitmap(transBitmap(res, checkedIconRes()));
		else
			return null;
	}
	
	private Bitmap transBitmap(Resources res, int drawableId){
		Bitmap org = BitmapFactory.decodeResource(res, drawableId);
		if(direction() != -1)
		{
			Matrix matrix = new Matrix();
			matrix.postRotate(direction());
			return Bitmap.createBitmap(org, 0, 0, org.getWidth(),
					org.getHeight(), matrix, true);
		}
		return org;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Markable){
			return ((Markable)o).ID().equals(ID());
		}
		return false;
	}

	
}
