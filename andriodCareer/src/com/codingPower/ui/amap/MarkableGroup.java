package com.codingPower.ui.amap;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLng;

public class MarkableGroup extends Markable {

	private String id;
	private int iconRes;
	private int checkIconRes;
	private LatLng center;
	private LinkedHashSet<Markable>  markableSet = new LinkedHashSet<Markable>();

	private MarkableGroup() {
	}

	@Override
	public String ID() {
		return id;
	}

	@Override
	public int iconRes() {
		if (markableSet.size() == 1) {
			return getOne().iconRes();
		} else {
			return iconRes;
		}
	}

	@Override
	public LatLng position() {
		if(center == null)
			center = getOne().position();
		return center;
	}

	public int getMarkSize(){
		return markableSet.size();
	}

	@Override
	public int checkedIconRes() {
		return checkIconRes;
	}

	@Override
	public int direction() {
		if (markableSet.size() == 1) {
			return getOne().direction();
		} else {
			return -1;
		}
	}

	public static MarkableGroup createdFrom(String key, Markable markable) {
		MarkableGroup group = new MarkableGroup();
		group.id = key;
		group.markableSet.add(markable);
		group.setGravity(CENTER);
		return group;
	}
	
	public static MarkableGroup createdFrom(String key, List<Markable> markable) {
		MarkableGroup group = new MarkableGroup();
		group.id = key;
		group.markableSet.addAll(markable);
		group.setGravity(CENTER);
		return group;
	}
	
	public void setIconRes(int iconRes){
		this.iconRes = iconRes;
	}
	
	public LatLng getCenter() {
		return center;
	}

	public void setCenter(LatLng center) {
		this.center = center;
	}

	public void add(Markable markable) {
		markableSet.add(markable);
	}

	public void clear() {
		markableSet.clear();
	}

	public Set<Markable> getMarkableSet() {
		return markableSet;
	}
	
	public Markable getOne(){
		return markableSet.iterator().next();
	}
	
	public void computePosition(){
		double lat = 0;
		double lon = 0;
		for(Markable mark : markableSet){
			LatLng latLng = mark.position();
			lat += latLng.latitude;
			lon += latLng.longitude;
		}
		center = new LatLng(lat / markableSet.size(), lon / markableSet.size());
	}
	
	public void computePosition(Projection proj){
		computePosition();
	}

}
