package com.codingPower.ui.amap;

import java.util.Set;

import com.amap.api.maps.model.LatLng;

/**
 * 地图聚合点击监听器
 * @author pengf
 *
 */
public interface MarkableGroupClickListener {

	/**
	 * 点击触发时
	 */
	public void onClicked(Set<Markable> set);
	
	/**
	 * 点击取消时
	 */
	public void onCancel(LatLng latLng);
}
