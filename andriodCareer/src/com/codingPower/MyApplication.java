package com.codingPower;

import com.codingPower.store.framework.Database;

import android.app.Application;

public class MyApplication extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();
		//数据库初始化方法，在应用启动前调用
		Database.init(this);
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}

	
}
