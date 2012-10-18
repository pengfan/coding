package com.codingPower.store.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.codingPower.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * 数据库持久层
 * @author pengfan
 *
 */
public class Database
{
	
	public static String dataRootPath ;
	/**
	 *DATABASE_NAME
	 */
	public static final String DATABASE_NAME = "test.db";
	/**
	 *DATABASE_VERSION
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 *projectName
	 */
	private static final String PROJECTNAME = "persistenceDemo";
	/**
	 *SYSTEM_DIRECTORY_DATA_DATABASES
	 */
	public static final String SYSTEM_DIRECTORY_DATA_DATABASES = "database";
	/**
	 *SYSTEM_DIRECTORY_DATA_ATTCHMENTS
	 */
	public static final String SYSTEM_DIRECTORY_DATA_ATTCHMENTS = "attchments";
	/**
	 *FILEROOTPATH
	 */
	public static final String FILEROOTPATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
			+ System.getProperty("file.separator") + PROJECTNAME + "/";
	
	public static final String TAG = "DATABASE";
	
	public static Properties createSqlMap = new Properties();
	/**
	 *ALLRESULT
	 */
	public static final int ALLRESULT = 0;
	private static Database database;
	private String dbname = DATABASE_NAME;

	public String getDbname()
	{
		return dbname;
	}

	public void setDbname(String dbname)
	{
		this.dbname = dbname;
	}

	/**
	 * Database
	 */
	private Database()
	{
		avaible();
	}
	/**
	 * 初始化必须要调用的方法
	 * @param context
	 */
	public static void init(Context context)
	{
		dataRootPath = context.getApplicationInfo().dataDir; 
		try
		{
			createSqlMap.loadFromXML(context.getResources().openRawResource(R.raw.db_init_sql));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return database
	 */
	public static Database getDatabase()
	{
		if (database == null)
		{
			database = new Database();
		}
		return database;
	}

	/**
	 * 
	 * @param databasename
	 *            databasename
	 */
	public Database(String databasename)
	{
		new Database();
		setDbname(databasename);
	}

	/**
	 * 是否可用
	 * 
	 * @return avaible
	 */
	public boolean avaible()
	{
		SDSQLiteOpenHelper sddb = null;
		SDSQLiteOpenHelper ctdb = null;
		File fpush = new File(dataRootPath + System.getProperty("file.separator"));
		if (!fpush.exists())
		{
			fpush.mkdir();
		}

		File fdb = new File(dataRootPath + System.getProperty("file.separator")
				+ SYSTEM_DIRECTORY_DATA_DATABASES + "/" + DATABASE_NAME);
		try
		{
			if (!fdb.exists())
			{
				new File(dataRootPath + System.getProperty("file.separator") + SYSTEM_DIRECTORY_DATA_DATABASES
						+ "/").mkdir();
				fdb.createNewFile();
			}
			sddb = creatHelper(sddb, DATABASE_NAME);

			File fdocs = new File(FILEROOTPATH + SYSTEM_DIRECTORY_DATA_ATTCHMENTS);
			if (android.os.Environment.getExternalStorageDirectory() != null && !fdocs.exists())
			{
				fdocs.mkdir();
			}

			return true;
		}
		catch (Exception e)
		{
			android.util.Log.e("Database", "check database fail", e);
			return false;

		}
		finally
		{
			if (sddb != null)
			{
				sddb.close();
			}
			if (ctdb != null)
			{
				ctdb.close();
			}
		}

	}

	/**
	 * 
	 * @param table
	 *            table
	 * @param args
	 *            args
	 * @return int
	 */
	public int insert(String table, ContentValues args)
	{

		SDSQLiteOpenHelper sddb = null;

		try
		{
			sddb = creatHelper(sddb, dbname);

			SQLiteDatabase db = sddb.getWritableDatabase();

			if (db == null)
			{
				return -1;
			}
			while (db.isDbLockedByCurrentThread() || db.isDbLockedByOtherThreads())
			{
				Thread.sleep(50);
			}
			return (int) db.insert(table, null, args);
		}
		catch (Exception e)
		{
			android.util.Log.e("database", "update database fail,table=" + table, e);

			return -1;
		}
		finally
		{
			if (sddb != null)
			{
				sddb.close();
			}
		}

	}

	/**
	 * 
	 * @param sddb
	 *            SDSQLiteOpenHelper
	 * @param databasename
	 *            databasename
	 * @return sddb
	 */
	private SDSQLiteOpenHelper creatHelper(SDSQLiteOpenHelper sddb, String databasename)
	{
		return new SDSQLiteOpenHelper(DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 
	 * @param sql
	 *            sql
	 * @param args
	 *            args
	 * @return int
	 */
	public int update(String sql, Object[] args)
	{
		if (args == null)
		{
			args = new Object[]
			{};
		}

		SDSQLiteOpenHelper sddb = null;

		try
		{
			sddb = creatHelper(sddb, dbname);

			SQLiteDatabase db = sddb.getWritableDatabase();

			if (db == null)
			{
				return -1;
			}
			boolean hasexe = false;
			while (!hasexe)
			{
				if (!db.isDbLockedByOtherThreads() && !db.isReadOnly())
				{
					db.execSQL(sql, args);
					hasexe = true;
				}
				else
				{
					Thread.sleep(100);
				}

			}

		}
		catch (Exception e)
		{
			android.util.Log.e("database", "update database fail,sql=" + sql, e);

			return -1;
		}
		finally
		{
			if (sddb != null)
			{
				sddb.close();
			}
		}

		return 0;
	}

	/**
	 * 
	 * @param map
	 *            HashMap
	 * @return int
	 */
	public int update(HashMap map)
	{

		SDSQLiteOpenHelper sddb = null;

		try
		{

			sddb = creatHelper(sddb, dbname);

			SQLiteDatabase db = sddb.getWritableDatabase();

			if (db == null)
			{
				return -1;
			}
			db.beginTransaction();
			while (db.isDbLockedByCurrentThread() || db.isDbLockedByOtherThreads())
			{
				Thread.sleep(50);
			}
			try
			{
				for (Iterator<Map.Entry<String, Object[]>> tmp = map.entrySet().iterator(); tmp.hasNext();)
				{
					Map.Entry<String, Object[]> mapvalue = tmp.next();
					String sql = mapvalue.getKey();
					Object[] args = mapvalue.getValue();
					if (args == null)
					{
						args = new Object[]
						{};
					}
					db.execSQL(sql, args);
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}

		}
		catch (Exception e)
		{

			return -1;
		}
		finally
		{
			if (sddb != null)
			{
				sddb.close();
			}
		}

		return 0;
	}

	/**
	 * 
	 * @param sql
	 *            sql
	 * @param args
	 *            args
	 * @return String[]
	 */
	public String[] query(String sql, String[] args)
	{
		if (args == null)
		{
			args = new String[]
			{};
		}

		List<String[]> list = querys(sql, args, 1);

		if (list == null)
		{
			return null;
		}

		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * 
	 * @param sql
	 *            sql
	 * @param args
	 *            args
	 * @return List<String[]>
	 */
	public List<String[]> querys(String sql, String[] args)
	{
		return querys(sql, args, 500);
	}

	/**
	 * 
	 * @param sql
	 *            sql
	 * @param args
	 *            args
	 * @param topmost
	 *            topmost
	 * @return List<String[]>
	 */
	public List<String[]> querys(String sql, String[] args, int topmost)
	{
		if (args == null)
		{
			args = new String[]
			{};
		}

		SDSQLiteOpenHelper sddb = null;

		try
		{
			sddb = creatHelper(sddb, dbname);

			SQLiteDatabase db = sddb.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, args); // 分页查询
			cursor.moveToFirst();

			int columnCnt = cursor.getColumnCount();
			List<String[]> result = new ArrayList<String[]>(cursor.getCount());

			while (!cursor.isAfterLast())
			{
				String[] row = new String[columnCnt];

				for (int k = 0; k < columnCnt; k++)
				{
					row[k] = cursor.getString(k);
				}

				result.add(row);
				cursor.moveToNext();

				/*
				 * if (topmost>0&&result.size() >= topmost) { break; }
				 */
			}

			cursor.close();

			return result;
		}
		catch (Exception e)
		{
			android.util.Log.e("database", "query database fail, sql=" + sql, e);

			return null;
		}
		finally
		{
			if (sddb != null)
			{
				sddb.close();
			}
		}
	}

	/**
	 * 批量数据的删除，修改，如果其中有一个数据插入失败则全部回滚。
	 * 
	 * @param sql
	 *            预编译语句
	 * @param list
	 *            数据列表
	 * @param binder
	 *            绑定器
	 */
	public void execute(String sql, Collection collection, SQLiteStatementBinder binder)
	{
		SDSQLiteOpenHelper sddb = null;
		sddb = creatHelper(sddb, dbname);
		SQLiteDatabase db = sddb.getWritableDatabase();
		db.beginTransaction();
		SQLiteStatement st = db.compileStatement(sql);
		try
		{
			for (Object o : collection)
			{
				binder.bind(st, o);
				st.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			st.close();
			if (sddb != null)
			{
				sddb.close();
			}
		}
	}
	
	/**
	 * 批量数据的插入如果其中有一个数据插入失败则全部回滚。
	 * 
	 * @param sql
	 *            预编译语句
	 * @param list
	 *            数据列表
	 * @param binder
	 *            绑定器
	 */
	public void executeInsert(String sql, Collection collection, SQLiteStatementBinder binder)
	{
		SDSQLiteOpenHelper sddb = null;
		sddb = creatHelper(sddb, dbname);
		SQLiteDatabase db = sddb.getWritableDatabase();
		db.beginTransaction();
		SQLiteStatement st = db.compileStatement(sql);
		try
		{
			for (Object o : collection)
			{
				binder.bind(st, o);
				st.executeInsert();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			st.close();
			if (sddb != null)
			{
				sddb.close();
			}
		}
	}

	/**
	 * 通过sql语句和数据库格式封装成对应对象,必须要满足三个条件<br/>
	 * 1、类必须要有无参构造函数。<br/>
	 * 2、类的每个属性的名字和字段名相同。<br/>
	 * 3、根据java数据类型和sqlite映射表获得sqlite基本数据类型 。 表见
	 * {@link #getSQLiteTypeByJavaType(Class) getSQLiteTypeByJavaType}<br/>
	 * 4、类的每个属性必须存在符合javabean标准的set方法。<br/>
	 * 
	 * @param className
	 *            类的完整名称，包含包名
	 * @param sql
	 *            查询的sql语句
	 * @param encode
	 *            编码方式
	 * @param proxy
	 *            代理对象 其如果想代理某个实体类的数据库方法，则方法名相同即可。
	 * @param args
	 *            查询sql的参数
	 * @return 结果的ArrayList
	 */

	@SuppressWarnings("unchecked")
	public List executeQuery(String className, String sql, String[] args, String encode, BaseDBProxy proxy)
	{
		Class clazz = null;
		ArrayList result = new ArrayList();
		try
		{
			clazz = Class.forName(className);
		}
		catch (ClassNotFoundException e1)
		{
			return result;
		}
		Cursor c = null;
		SDSQLiteOpenHelper sddb = null;
		try
		{
			sddb = creatHelper(sddb, dbname);
			c = sddb.getReadableDatabase().rawQuery(sql, args);
			if (c != null && c.getCount() >= 0)
			{
				int colcnt = c.getColumnCount();
				Method[] methods = new Method[colcnt];
				boolean[] isProxyer = new boolean[colcnt];
				for (int i = 0; i < colcnt; i++)
				{
					String name = c.getColumnName(i);
					Field f = null;
					String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
					try
					{
						f = clazz.getDeclaredField(name);
						Class fc = f.getType();
						Method proxyMethod = null;
						if (proxy != null)
						{
							try
							{
								proxyMethod = proxy.getClass().getMethod(methodName, getSQLiteTypeByJavaType(fc));
							}
							catch (NoSuchMethodException e)
							{
								e.getMessage();
							}
						}
						if (proxyMethod == null)
						{
							methods[i] = clazz.getMethod(methodName, getSQLiteTypeByJavaType(fc));
						}
						else
						{
							methods[i] = proxyMethod;
							isProxyer[i] = true;
						}
					}
					catch (Exception e)
					{
						e.getMessage();
					}
				}
				if (!c.moveToFirst())
				{
					return result;
				}

				while (!c.isAfterLast())
				{
					Object o = clazz.newInstance();
					if (proxy != null)
					{
						proxy.setProvider(o);
					}
					for (int i = 0; i < colcnt; i++)
					{
						if (methods[i] != null)
						{
							Object v = getSqliteData(c, i, methods[i]);
							if (encode != null && v != null && v.getClass() == String.class)
							{
								v = new String(((String) v).getBytes("GB2312"), encode);
							}
							if (isProxyer[i])
							{
								methods[i].invoke(proxy, v);
							}
							else
							{
								methods[i].invoke(o, v);
							}
						}
					}
					result.add(o);
					c.moveToNext();
				}
			}
			return result;
		}
		catch (Exception e)
		{
			Log.i(TAG, e.getMessage());
			return new ArrayList();
		}
		finally
		{
			try
			{
				c.close();
				sddb.close();
			}
			catch (Exception e)
			{
				e.getMessage();
			}
		}
	}

	/**
	 * 通过sql语句和数据库格式封装成对应对象,必须要满足三个条件<br/>
	 * 1、类必须要有无参构造函数。<br/>
	 * 2、类的每个属性的名字和字段名相同。<br/>
	 * 3、根据java数据类型和sqlite映射表获得sqlite基本数据类型 。<br/>
	 * 4、类的每个属性必须存在符合javabean标准的set方法。<br/>
	 * 
	 * @param className
	 *            类的完整名称，包含包名
	 * @param sql
	 *            查询的sql语句
	 * @param args
	 *            args
	 * @param proxy
	 *            BaseDBProxy
	 * @return 结果的ArrayList
	 * @see #getSQLiteTypeByJavaType(Class)
	 */
	public List executeQuery(String className, String sql, String[] args, BaseDBProxy proxy)
	{
		return executeQuery(className, sql, args, null, proxy);
	}

	// 本应按照以下标准设计数据库，但是现有数据库全部是使用字符串存储，所以该方法先修改为全部返回String.class
	/**
	 * 本应用中从java的数据类型获得到保存在sqllite的数据类型
	 * <table>
	 * <tr>
	 * <td>java类型</td>
	 * <td>sqlite类型</td>
	 * </tr>
	 * <tr>
	 * <td>String</td>
	 * <td>String</td>
	 * </tr>
	 * <tr>
	 * <td>Date</td>
	 * <td>integer</td>
	 * </tr>
	 * <tr>
	 * <td>boolean</td>
	 * <td>integer</td>
	 * </tr>
	 * <tr>
	 * <td>String[]</td>
	 * <td>String</td>
	 * </tr>
	 * <tr>
	 * <td>Integer</td>
	 * <td>Integer</td>
	 * </tr>
	 * <tr>
	 * <td>其它</td>
	 * <td>其它</td>
	 * </tr>
	 * </table>
	 * 
	 * @param clazz
	 * Class
	 * @return Class
	 */
	@SuppressWarnings("unchecked")
	public static Class getSQLiteTypeByJavaType(Class clazz)
	{
		return String.class;
		/*
		 * if(clazz.equals(Date.class) || clazz.equals(Boolean.class)) { return
		 * Integer.class; } else if(clazz.equals(String[].class)) { return
		 * String.class; } else { return clazz; }
		 */
	}

	/**
	 * 根据类型获得相应的数据
	 * 
	 * @param c
	 *            数据库游标
	 * @param i
	 *            列序
	 * @param method
	 *            方法
	 * @return 数据
	 */
	public static Object getSqliteData(Cursor c, int i, Method method)
	{
		Class clazz = method.getParameterTypes()[0];
		if (clazz.equals(String.class))
		{
			return c.getString(i);
		}
		if (clazz.equals(Integer.class))
		{
			return c.getInt(i);
		}
		if (clazz.equals(Long.class))
		{
			return c.getLong(i);
		}
		if (clazz.equals(Double.class))
		{
			return c.getDouble(i);
		}
		if (clazz.equals(Float.class))
		{
			return c.getFloat(i);
		}
		return c.getString(i);
	}

	/**
	 * 
	 * @param sql
	 *            sql
	 * @return count
	 */
	public int queryCount(String sql)
	{
		int countNumber = 0;

		SDSQLiteOpenHelper sddb = null;
		try
		{
			sddb = creatHelper(sddb, dbname);
			SQLiteDatabase db = null;
			if (sddb != null)
			{
				db = sddb.getReadableDatabase();
			}
			Cursor cursor = null;
			if (db != null)
			{
				cursor = db.rawQuery(sql, null);
			}
			if (cursor != null)
			{
				int count = cursor.getCount();
				if (count > 0)
				{
					cursor.moveToPosition(0);
					countNumber = cursor.getInt(0);
				}
				cursor.close();
			}
		}
		catch (Exception e)
		{
			e.getMessage();
		}
		finally
		{
			try
			{
				if (sddb != null)
				{
					sddb.close();
				}
			}
			catch (Exception error)
			{
				error.getMessage();
			}
		}
		return countNumber;
	}

	/**
	 * createContactCaches
	 */
	public void createContactCaches()
	{
		SDSQLiteOpenHelper sddb = null;
		try
		{
			sddb = creatHelper(sddb, dbname);
			SQLiteDatabase db = null;
			if (sddb != null)
			{
				db = sddb.getWritableDatabase();
			}
			sddb.createContactCaches(db);
		}
		catch (Exception e)
		{
			e.getMessage();
		}
		if (sddb != null)
		{
			sddb.close();
		}
	}

}
