package com.codingPower.store;

import java.util.List;
import java.util.Properties;

import android.content.Context;

import com.codingPower.R;
import com.codingPower.store.framework.BaseDBProxy;
import com.codingPower.store.framework.Database;
import com.codingPower.store.model.Info;

public class InfoManager
{
	public static Properties sqlMap;
	private InfoDBProxy proxyer;
	
	public void init(Context context)
	{
		sqlMap = new Properties();
		
		try
		{
			sqlMap.loadFromXML(context.getResources().openRawResource(R.raw.info_sql));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		proxyer = new InfoDBProxy();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Info> query()
	{
		Database db = Database.getDatabase();
		return db.executeQuery("com.codingPower.store.model.Info", sqlMap.getProperty("info.getAll"), new String[]{}, proxyer);
	}
	
	private static class InfoDBProxy extends BaseDBProxy
	{

		public Info getInfo()
		{
			return (Info) provider;
		}
		
		public String isGender()
		{
			return parseBooleanToString(getInfo().isGender());
		}
		public void setGender(String gender)
		{
			getInfo().setGender(parseStringToBoolean(gender));
		}
		public String getHobbies()
		{
			return parseArrayToString(getInfo().getHobbies());
		}
		public void setHobbies(String hobbies)
		{
			getInfo().setHobbies(parseStringToArray(hobbies));
		}
		public String getBirthday()
		{
			return parseDateToString(getInfo().getBirthday());
		}
		public void setBirthday(String birthday)
		{
			getInfo().setBirthday(parseStringToDate(birthday));
		}
		public String getAge()
		{
			return parseIntToString(getInfo().getAge());
		}
		public void setAge(String age)
		{
			getInfo().setAge(parseInteger(age));
		}
		
	}
}
