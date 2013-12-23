package com.codingPower;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.codingPower.store.framework.Database;
import com.codingPower.utils.JSONBeanUtil;

public class MyApplication extends Application
{
	private static final String CACHE_NAME = "MyApplication";
	private Map<String, Object> mSession = new HashMap<String, Object>();
	
	@Override
	public void onCreate(){
		super.onCreate();
		//数据库初始化方法，在应用启动前调用
		Database.init(this);
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}
	
	/**
	 * Session数据保持和持久化，简单持久化
	 * 
	 * @param key
	 * @param val
	 * @param clazz
	 */
	public void putToSession(String key, Object val, Class clazz) {
		mSession.put(key, val);
		String json = JSONBeanUtil.getJsonFromObject(val);
		if (json != null) {
			getSharedPreferences(CACHE_NAME, 0).edit().putString(key, json)
					.putString(key + "_className", clazz.getName()).commit();
		}
	}

	public Object getFromSession(String key) {
		Object val = mSession.get(key);
		try {
			if (val == null) {
				String jsonClassName = getSharedPreferences(CACHE_NAME, 0)
						.getString(key + "_className", null);
				String jsonval = getSharedPreferences(CACHE_NAME, 0).getString(
						key, null);
				if (jsonval != null && jsonClassName != null) {
					Class clazz = Class.forName(jsonClassName);
					val = JSONBeanUtil.getObjectFromJson(jsonval, clazz);
					if (val != null) {
						mSession.put(key, val);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return val;
	}

	
}
