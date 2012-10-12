package com.codingPower.store.framework;

import android.database.sqlite.SQLiteStatement;

/**
 * SQLite的statement绑定器。
 * @author pengfan
 *
 */
public abstract class SQLiteStatementBinder
{
	public abstract void bind(SQLiteStatement st, Object o);
	
	public void bindString(SQLiteStatement st, int index ,String str)
	{
		if(str == null)
		{
			st.bindNull(index);
		}
		else
		{
			st.bindString(index, str);
		}
	}
	public void bindLong(SQLiteStatement st, int index ,long v)
	{
		st.bindLong(index, v);
	}
	public void bindDouble(SQLiteStatement st, int index ,double v)
	{
		st.bindDouble(index, v);
	}
	public void bindNull(SQLiteStatement st, int index)
	{
		st.bindNull(index);
	}
	
	public String[] getArgs(Object o)
	{
		return new String[0];
	}
}
