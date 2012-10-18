package com.codingPower.store.framework;

import java.util.Date;

/**
 * 所有数据库代理类的父类，利用此代理类可以将数据库持久化逻辑和业务逻辑分离
 * 
 * @author pengfan
 * 
 */
public class BaseDBProxy
{
	protected Object provider = null;
	protected int numberDafaultValue = 0;

	public void setProvider(Object provider)
	{
		this.provider = provider;
	}

	/**
	 * 
	 * @param value
	 *            value
	 * @return parseInteger
	 */
	protected int parseInteger(String value)
	{
		try
		{
			if (value != null)
			{
				return Integer.parseInt(value);
			}
		}
		catch (NumberFormatException e)
		{
			e.getMessage();
		}
		return numberDafaultValue;
	}
	
	protected long parseLong(String value)
	{
		try
		{
			if (value != null)
			{
				return Long.parseLong(value);
			}
		}
		catch (NumberFormatException e)
		{
			e.getMessage();
		}
		return numberDafaultValue;
	}
	
	/**
	 * 
	 * @param values
	 * values
	 * @return String
	 */
	protected String parseArrayToString(String[] values)
	{
		StringBuffer sb = new StringBuffer();
		for (String v : values)
		{
			sb.append(",").append(v);
		}
		if (sb.length() > 0)
		{
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param value 
	 * String
	 * @return String[]
	 */
	protected String[] parseStringToArray(String value)
	{
		return value.split(",");
	}
	/**
	 * 
	 * @param value
	 * String
	 * @return boolean
	 */
	protected boolean parseStringToBoolean(String value)
	{
		if ("1".equals(value))
		{
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param value
	 * int
	 * @return String
	 */
	protected String parseIntToString(int value)
	{
		return Integer.toString(value);
	}
	
	protected String parseLongToString(long value)
	{
		return Long.toString(value);
	}
	
	
	/**
	 * 
	 * @param value
	 * boolean
	 * @return String
	 */
	protected String parseBooleanToString(boolean value)
	{
		return value ? "1" : "0";
	}
	
	protected String parseDateToString(Date date)
	{
		return Long.toString(date.getTime());
	}
	
	protected Date parseStringToDate(String date)
	{
		long time = Long.parseLong(date);
		return new Date(time);
	}
}
