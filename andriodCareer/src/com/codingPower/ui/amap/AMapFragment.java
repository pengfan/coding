package com.codingPower.ui.amap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.codingPower.R;
import com.codingPower.ui.base.BaseFragment;

public class AMapFragment extends BaseFragment implements OnMapLoadedListener,
		OnMarkerClickListener, OnMapClickListener, OnCameraChangeListener {

	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private Handler mTriggerHander;

	private static LatLng mDefaultCenter = new LatLng(32.0223, 118.782);
	private static int minTriggerTime = 1000;
	private int mDefaultZoom = 14;
	private int mGroupPixal = 200;//多少像素内的点聚合成一个
	private boolean noActiveSurrondingChange;//点击之后的状态
	private Markable focusMark;
	private CameraPosition lastCp;
	private GroupTask mGroupTask;
	private boolean mGrouping;
	private boolean mLoaded;

	private Map<String, Markable> mAMarkMap = new HashMap<String, Markable>();
	private Map<String, MarkableGroup> mMarkableGroup = new HashMap<String, MarkableGroup>();
	private Map<String, Path> mPathMap = new HashMap<String, Path>();

	private InfoViewAdapter mInfoViewAdapter;
	private OnSurroundingChangedListener mOnSurroundingChangedListener;
	private MarkableGroupClickListener mMarkableGroupClickListener;
	private MarkableClickListener mMarkableClickListener;
	private MarkViewAdapter mMarkGroupViewAdapter;

	@Override
	public void onMapLoaded() {
		CameraPosition.Builder builder = new CameraPosition.Builder()
		.target(mDefaultCenter)
		.zoom(mDefaultZoom);
		aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
		mLoaded = true;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTriggerHander = new Handler();
		aMap.setOnMapLoadedListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnCameraChangeListener(this);
		if (mInfoViewAdapter != null) {
			setInfoViewAdapter(mInfoViewAdapter);
		}
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = onCreateMapContainer(inflater, container,
				savedInstanceState);
		setupMap(view, savedInstanceState);
		return view;
	}
	
	private void setupMap(View view, Bundle savedInstanceState) {
		mapView = (MapView) view.findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		mUiSettings = aMap.getUiSettings();
		mUiSettings.setRotateGesturesEnabled(false);
	}

	/**
	 * 构造带有mapView的控件
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	protected View onCreateMapContainer(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	public boolean isMapLoad(){
		return mLoaded;
	}

	public void init(LatLng defaultCenter, int defaultZoom) {
		mDefaultCenter = defaultCenter;
		mDefaultZoom = defaultZoom;
	}

	public void setInfoViewAdapter(InfoViewAdapter adapter) {
		mInfoViewAdapter = adapter;
		if (aMap != null) {
			aMap.setOnInfoWindowClickListener(mInfoViewAdapter);
			aMap.setInfoWindowAdapter(mInfoViewAdapter);
		}
	}

	public void setMarkableGroupClickListener(
			MarkableGroupClickListener listener) {
		mMarkableGroupClickListener = listener;
	}

	public void setMarkGroupViewAdapter(MarkViewAdapter groupAdapter) {
		mMarkGroupViewAdapter = groupAdapter;
	}
	
	public void showInfoView(Markable mark){
		mark.getMarker().showInfoWindow();
	}

	@Override
	public void onCameraChange(CameraPosition cp) {
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cp) {
		Projection proj = aMap.getProjection();
		final LatLngBounds bounds = proj.getVisibleRegion().latLngBounds;

		Log.i("scalePerPixel",
				"onCameraChangeFinish : " + aMap.getScalePerPixel());
		//放大和缩小地图尺寸触发重新排列全部分组
		if (mOnSurroundingChangedListener != null && !noActiveSurrondingChange ) {
			// 获取可视区域
			//调节经纬度改变触发频率。
			if (lastCp == null
					|| lastCp != null
					&& (AMapUtils.calculateLineDistance(lastCp.target,
							cp.target) > 100 || Math.abs(Float.compare(
							lastCp.zoom, cp.zoom)) >= 1)) {
				//设定最小触发时间为1秒，1秒内不再移动地图才会触发
				mTriggerHander.removeCallbacksAndMessages(null);
				mTriggerHander.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mOnSurroundingChangedListener.changed(bounds.southwest,
								bounds.northeast);
					}
				}, minTriggerTime);
				
			}
		}

		lastCp = cp;
		noActiveSurrondingChange = false;
	}

	public int getGroupSize() {
		return mMarkableGroup.size();
	}

	public static void setDefaultLocation(double lat, double lon){
		mDefaultCenter = new LatLng(lat, lon);
	}

	public void setOnSurroundingChangedListener(
			OnSurroundingChangedListener listener) {
		mOnSurroundingChangedListener = listener;
	}

	public void setOnMarkableClickListener(MarkableClickListener listener) {
		mMarkableClickListener = listener;
	}

	public LatLng getCenter() {
		return aMap.getCameraPosition().target;
	}

	public float getZoomLevel() {
		return aMap.getCameraPosition().zoom;
	}

	public Path getPath(String name) {
		return mPathMap.get(name);
	}
	
	public boolean isGrouping(){
		return mGrouping;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAMarkMap.clear();
	}

	public void track(Path path) {
		path.setPolyLine(aMap.addPolyline(new PolylineOptions().addAll(
				path.getPointList()).color(path.getColor())));
		mPathMap.put(path.getName(), path);
		if (path.getStartMark() != null)
			addMark(path.getStartMark());
		if (path.getEndMark() != null)
			addMark(path.getEndMark());
	}

	/**
	 * 将mark钉到地图上 不参与聚合
	 * 
	 * @param aMark
	 */
	public void nail(Markable mark) {
		String id = mark.ID();
		if (mAMarkMap.containsKey(id)) {
			Markable lm = mAMarkMap.get(id);
			if (AMapUtils.calculateLineDistance(lm.position(),
					mark.position()) > 5) {
				lm.getMarker().setPosition(mark.position());
			}
			if (mark.iconRes() != lm.iconRes()
					|| mark.direction() != lm.direction()) {
				lm.getMarker().setIcon(mark.icon(getResources()));
			}
			lm.getMarker().setObject(mark);
			mark.setMarker(lm.getMarker());
		} else {
			addMark(mark);
		}
		mAMarkMap.put(id, mark);
	}

	public void autoZoom(Collection<? extends Markable> collection) {
		if (!collection.isEmpty()) {
			if(collection.size() == 1){
				centerTo(collection.iterator().next().position());
			}
			else{
				LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
				double distance = 0.0;
				LatLng firstLatLng = null;
				for (Markable mark : collection) {
					LatLng pos = mark.position();
					boundsBuilder.include(pos);
					if(firstLatLng == null){
						firstLatLng = pos;						
					}else{
						double t_distance = AMapUtils.calculateLineDistance(pos, firstLatLng);
						if(t_distance > distance){
							distance = t_distance;
						}
					}
				}
				if(distance < 0.1){
					centerTo(firstLatLng);
				}else{
					aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
							boundsBuilder.build(), 30));
				}
			}
			
		}
	}
	
	public void autoZoomLatLng(Collection<LatLng> collection) {
		if (!collection.isEmpty()) {
			double distance = 0.0;
			LatLng firstLatLng = null;
			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
			for (LatLng latLng : collection) {
				boundsBuilder.include(latLng);
				if(firstLatLng == null){
					firstLatLng = latLng;						
				}else{
					double t_distance = AMapUtils.calculateLineDistance(latLng, firstLatLng);
					if(t_distance > distance){
						distance = t_distance;
					}
				}
			}
			if(distance < 0.1){
				centerTo(firstLatLng);
			}else{
				CameraUpdate update = CameraUpdateFactory.newLatLngBounds(
						boundsBuilder.build(), 30);
				aMap.moveCamera(update);
			}
		}
	}

	public Markable get(String id) {
		return mAMarkMap.get(id);
	}

	public boolean containsId(String id) {
		return mAMarkMap.containsKey(id);
	}

	public void remove(String id) {
		Markable mark = mAMarkMap.get(id);
		mark.getMarker().setObject(null);
		mark.getMarker().remove();
		mark.setMarker(null);
		mAMarkMap.remove(id);
	}

	public void centerTo(LatLng latlng) {
		aMap.moveCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition.Builder().target(latlng)
						.zoom(aMap.getCameraPosition().zoom).build()));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Markable mark = (Markable) marker.getObject();
		noActiveSurrondingChange = true;
		if (focusMark != mark) {
			clearFocus();
			if (mark.checkedIconRes() > 0) {
				marker.setIcon(mark.checkedIcon(getResources()));
			}

			focusMark = mark;
		}
		if (mMarkableGroupClickListener != null
				&& mark instanceof MarkableGroup) {
			MarkableGroup group = (MarkableGroup) mark;
			mMarkableGroupClickListener.onClicked(group.getMarkableSet());
		} else if (mMarkableClickListener != null) {
			mMarkableClickListener.onClicked(mark);
		}
		return false;
	}

	@Override
	public void onMapClick(LatLng latLng) {
		if (focusMark != null && focusMark.getMarker() != null) {
			if (focusMark.getMarker().isInfoWindowShown()) {
				focusMark.getMarker().hideInfoWindow();
			}
			if (mMarkableGroupClickListener != null) {
				mMarkableGroupClickListener.onCancel(latLng);
			}
			if (mMarkableClickListener != null) {
				mMarkableClickListener.onCancel(latLng);
			}
		}
		clearFocus();
	}

	public void removeAll() {
		cancelMarkEvents();
		for (Markable m : mAMarkMap.values()) {
			removeMark(m);
		}
		for (MarkableGroup g : mMarkableGroup.values()) {
			removeMark(g);
		}
		mAMarkMap.clear();
		mMarkableGroup.clear();
	}

	/**
	 * 超过最大容量统一删除最远的
	 * 
	 * @param max
	 */
	public void removeFarthestMarkOver(int max) {
		if (mAMarkMap.size() > max) {
			List<DistanceMarkable> resList = new ArrayList<DistanceMarkable>();
			LatLng center = getCenter();
			Iterator<String> iterator = mAMarkMap.keySet().iterator();
			while (iterator.hasNext()) {
				String id = iterator.next();
				Markable markable = mAMarkMap.get(id);
				float distance = AMapUtils.calculateLineDistance(center,
						markable.getMarker().getPosition());
				addToSortList(new DistanceMarkable(distance, id), resList);
			}
			for (int i = 0; i < mAMarkMap.size() - max; i++) {
				Markable markable = mAMarkMap.get(resList.get(i).key);
				if (markable != focusMark)
					remove(resList.get(i).key);
			}
		}
	}

	private void removeMark(Markable mark) {
		if (mark.getMarker() != null) {
			mark.getMarker().setObject(null);
			mark.getMarker().remove();
			mark.setMarker(null);
		}
	}

	/**
	 * 添加Markable到地图上
	 * 
	 * @param mark
	 */
	private void addMark(Markable mark) {
		BitmapDescriptor icon;
		if (mark instanceof MarkableGroup && mMarkGroupViewAdapter != null) {
			MarkableGroup group = (MarkableGroup) mark;
			View view = mMarkGroupViewAdapter.getView(
					LayoutInflater.from(getActivity()), group);
			if (group.getMarkSize() > 1)
				icon = BitmapDescriptorFactory.fromView(view);
			else
				icon = mark.icon(getResources());
		} else {
			icon = mark.icon(getResources());
		}
		Marker marker = aMap.addMarker(new MarkerOptions().icon(icon)
				.position(mark.position()).title(mark.ID())//必须加title，否则无法触发点击弹出框
				.perspective(true));
		if (mark.getGravity() == Markable.CENTER) {
			marker.setAnchor(0.5f, 0.5f);
		}
		marker.setObject(mark);
		mark.setMarker(marker);
	}

	/**
	 * 按照从大到小排序
	 * 
	 * @param cur
	 * @param list
	 */
	private void addToSortList(DistanceMarkable cur, List<DistanceMarkable> list) {
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if (cur.distance > list.get(i).distance) {
				index = i;
				break;
			}
		}
		list.add(index, cur);
	}

	private static class DistanceMarkable {
		float distance;
		String key;

		public DistanceMarkable(float distance, String key) {
			this.distance = distance;
			this.key = key;
		}
	}

	private void clearFocus() {
		if (focusMark != null && focusMark.getMarker() != null) {
			focusMark.getMarker().setIcon(focusMark.icon(getResources()));
		}
		focusMark = null;
	}
	
	protected void cancelMarkEvents(){
		if(mMarkableGroupClickListener != null){
			mMarkableGroupClickListener.onCancel(getCenter());
		}
		if(mMarkableClickListener != null){
			mMarkableClickListener.onCancel(getCenter());
		}
	}
	
	public void groupingBatch(Collection<? extends Markable> collection, boolean autoZoom){
		mGrouping = true;
		if(collection.isEmpty()){
			removeAll();
			mGrouping = false;
		}
		else if(autoZoom){
			aMap.setOnCameraChangeListener(new OneTimeMapSizeChangedListener(collection));
			autoZoom(collection);
		}
		else{
			getGrouping(collection);
		}
	}
	
	private void getGrouping(Collection<? extends Markable> collection){
		if(mGroupTask == null){
			mGroupTask = new GroupTask();
			mGroupTask.execute(collection);
		}
		else{
			mGroupTask.cancel(true);
			mGroupTask = new GroupTask();
			mGroupTask.execute(collection);
		}
	}

	/**
	 * 周边改变的监听器
	 */
	public static interface OnSurroundingChangedListener {
		public void changed(LatLng southwestPos, LatLng northeastPos);
	}
	
	/**
	 * 用于监听地图发生非主动缩放变化时
	 * @author pengf
	 *
	 */
	private class OneTimeMapSizeChangedListener implements OnCameraChangeListener{
		Collection<? extends Markable> marks;
		
		private OneTimeMapSizeChangedListener(Collection<? extends Markable> marks){
			this.marks = marks;
		}

		@Override
		public void onCameraChange(CameraPosition cp) {
		}

		@Override
		public void onCameraChangeFinish(CameraPosition cp) {
			aMap.setOnCameraChangeListener(null);
			mUiSettings.setScrollGesturesEnabled(false);
			getGrouping(marks);			
		}
	}

	private class GroupTask extends
			AsyncTask<Collection<? extends Markable>, Object, Map<String, MarkableGroup>> {

		@Override
		protected Map<String, MarkableGroup> doInBackground(
				Collection<? extends Markable>... params) {
			Collection<? extends Markable> markCollection = params[0];
			try {
				return groupAll(markCollection, mGroupPixal);
			} catch (TaskCanceledException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Map<String, MarkableGroup> result) {
			synchronized (AMapFragment.this) {
				if(!isCancelled() && isAdded() && result != null){
					removeAll();
					mMarkableGroup = result;
					for (MarkableGroup group : result.values()) {
						if (group.getMarker() == null) {
							addMark(group);
						} else if (mMarkGroupViewAdapter != null) {
							View view = mMarkGroupViewAdapter.getView(
									LayoutInflater.from(getActivity()), group);
							group.getMarker().setIcon(
									BitmapDescriptorFactory.fromView(view));
						}
					}
				}
				mGrouping = false;
				aMap.setOnCameraChangeListener(AMapFragment.this);
				mUiSettings.setScrollGesturesEnabled(true);
			}
		}
		
		private double approxDistanceCoord(LatLng loc1, LatLng loc2, Projection proj) throws TaskCanceledException{
			if(isCancelled())
				throw new TaskCanceledException();
			Point mapPoint1 = proj.toScreenLocation(loc1);
			Point p1 = new Point(), p2= new Point();
			p1 = mapPoint1;
			
			Point mapPoint2 = proj.toScreenLocation(loc2);
			p2 = mapPoint2;
			
			int dx = Math.abs(p1.x - p2.x);
			int dy = Math.abs(p1.y - p2.y);
			if(dx < dy){
				return dx + dy - dx / 2;
			}else{
				return dx + dy - dy / 2;
			}
		}
		
		private List<Markable> findNeighboursForAnnotation(Markable mark,
				List<Markable> neighbourhood , double distance, Projection proj){
			List<Markable> result = new ArrayList<Markable>();
			for(Markable cMark: neighbourhood ){
				if(cMark == mark){
					continue;
				}
				double d =  approxDistanceCoord(cMark.position(), mark.position(), proj);
				if(d < distance){
					result.add(cMark);
				}
				
			}
			if(result.size() == 0)
				return null;
			return result;
		}
		
		private Map<String, MarkableGroup> groupAll(Collection<? extends Markable> markList, double distance){
			Map<String, MarkableGroup> result = new HashMap<String, MarkableGroup>();
			List<Markable> processed = new ArrayList<Markable>();
			List<Markable> restOfAnnotations =new ArrayList<Markable>(markList);
			Projection proj = aMap.getProjection();
			int id = 0;
			for(Markable cMark: markList){
				if(processed.contains(cMark)){
					continue;
				}
				processed.add(cMark);
				List<Markable> neighbours = 
						findNeighboursForAnnotation(cMark, restOfAnnotations, distance, proj);
				String key = String.valueOf(id);
				if(neighbours == null){
					MarkableGroup group = MarkableGroup.createdFrom(key, cMark);
					result.put(key, group);
					group.computePosition();
				}
				else{
					processed.addAll(neighbours);
					neighbours.add(cMark);
					MarkableGroup group = MarkableGroup.createdFrom(key, neighbours);
					result.put(key, group);
					group.computePosition();
				}
				restOfAnnotations.removeAll(processed);
				id++;
			}
			return result;
		}

	}
}
