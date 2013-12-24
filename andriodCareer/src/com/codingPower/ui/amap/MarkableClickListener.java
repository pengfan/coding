package com.codingPower.ui.amap;

import com.amap.api.maps.model.LatLng;

/**
 * 地图标记点击时触发
 * @author pengf
 *
 */
public interface MarkableClickListener<T> {

	/**
	 * 点击触发时
	 */
	public void onClicked(T markable);
	
	/**
	 * 点击取消时
	 */
	public void onCancel(LatLng latLng);
}
