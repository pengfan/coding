package com.codingPower.store.framework;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * SD卡的sqliteOpenHelper
 * @author pengfan
 *
 */
public class SDSQLiteOpenHelper
{
	private static final String TAG = SDSQLiteOpenHelper.class.getSimpleName();
	private final String mName;
	private final CursorFactory mFactory;
	private final int mNewVersion;
	private SQLiteDatabase mDatabase = null;
	private boolean mIsInitializing = false;

	/**
	 * 
	 * @param name
	 *            name
	 * @param factory
	 *            factory
	 * @param version
	 *            version
	 */
	public SDSQLiteOpenHelper(String name, CursorFactory factory, int version)
	{
		if (version < 1)
		{
			throw new IllegalArgumentException("Version must be >= 1, was " + version);
		}

		mName = name;
		mFactory = factory;
		mNewVersion = version;
	}

	/**
	 * Create and/or open a database that will be used for reading and writing.
	 * Once opened successfully, the database is cached, so you can call this
	 * method every time you need to write to the database. Make sure to call
	 * {@link #close} when you no longer need it.
	 * <p>
	 * Errors such as bad permissions or a full disk may cause this operation to
	 * fail, but future attempts may succeed if the problem is fixed.
	 * </p>
	 * 
	 * @throws SQLiteException
	 *             if the database cannot be opened for writing
	 * @return a read/write database object valid until {@link #close} is called
	 */
	public synchronized SQLiteDatabase getWritableDatabase()
	{
		boolean cycle = true;
		while (cycle)
		{
			if ((mDatabase != null) && mDatabase.isOpen() && !mDatabase.isReadOnly())
			{

				return mDatabase; // The database is already open for business
			}
			else
			{
				if ((mDatabase != null) && mDatabase.isOpen() && mDatabase.isReadOnly())
				{
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					cycle = false;
				}
			}
		}

		if (mIsInitializing)
		{
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		// If we have a read-only database open, someone could be using it
		// (though they shouldn't), which would cause a lock to be held on
		// the file, and our attempts to open the database read-write would
		// fail waiting for the file lock. To prevent that, we acquire the
		// lock on the read-only database, which shuts out other users.
		boolean success = false;
		SQLiteDatabase db = null;

		try
		{
			mIsInitializing = true;

			if (mName == null)
			{
				db = SQLiteDatabase.create(null);
			}
			else
			{
				String path = getDatabasePath(mName).getPath();
				db = SQLiteDatabase.openOrCreateDatabase(path, mFactory);
			}

			int version = db.getVersion();

			if (version != mNewVersion)
			{
				db.beginTransaction();

				try
				{
					if (version == 0)
					{
						onCreate(db);
					}
					else
					{
						onUpgrade(db, version, mNewVersion);
					}

					db.setVersion(mNewVersion);
					db.setTransactionSuccessful();
				}
				finally
				{

					db.endTransaction();
				}
			}

			onOpen(db);
			success = true;

			return db;
		}
		finally
		{
			mIsInitializing = false;

			if (success)
			{
				if (mDatabase != null)
				{
					try
					{
						mDatabase.close();
					}
					catch (Exception e)
					{
						e.getMessage();
					}
				}

				mDatabase = db;
			}
			else
			{
				if (db != null)
				{
					db.close();
				}
			}
		}
	}

	/**
	 * Create and/or open a database. This will be the same object returned by
	 * {@link #getWritableDatabase} unless some problem, such as a full disk,
	 * requires the database to be opened read-only. In that case, a read-only
	 * database object will be returned. If the problem is fixed, a future call
	 * to {@link #getWritableDatabase} may succeed, in which case the read-only
	 * database object will be closed and the read/write object will be returned
	 * in the future.
	 * 
	 * @throws SQLiteException
	 *             if the database cannot be opened
	 * @return a database object valid until {@link #getWritableDatabase} or
	 *         {@link #close} is called.
	 */
	public synchronized SQLiteDatabase getReadableDatabase()
	{
		if ((mDatabase != null) && mDatabase.isOpen())
		{
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing)
		{
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		try
		{
			return getWritableDatabase();
		}
		catch (SQLiteException e)
		{
			if (mName == null)
			{
				throw e; // Can't open a temp database read-only!
			}

			Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
		}

		SQLiteDatabase db = null;

		try
		{
			mIsInitializing = true;

			String path = getDatabasePath(mName).getPath();
			db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READWRITE);

			if (db.getVersion() != mNewVersion)
			{
				throw new SQLiteException("Can't upgrade read-only database from version " + db.getVersion() + " to "
						+ mNewVersion + ": " + path);
			}

			onOpen(db);
			Log.w(TAG, "Opened " + mName + " in read-only mode");
			mDatabase = db;

			return mDatabase;
		}
		finally
		{
			mIsInitializing = false;

			if ((db != null) && (db != mDatabase))
			{
				db.close();
			}
		}
	}

	/**
	 * Close any open database object.
	 */
	public synchronized void close()
	{
		if (mIsInitializing)
		{
			throw new IllegalStateException("Closed during initialization");
		}

		if ((mDatabase != null) && mDatabase.isOpen())
		{
			mDatabase.close();
			mDatabase = null;
		}
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @return File
	 */
	public File getDatabasePath(String name)
	{
		return new File(Database.dataRootPath + System.getProperty("file.separator")// 实际需要放入内存中Database.FILEROOTPATH
				+ Database.SYSTEM_DIRECTORY_DATA_DATABASES + "/" + name);
	}

	/**
	 * Called when the database is created for the first time. This is where the
	 * creation of tables and the initial population of the tables should
	 * happen.
	 * 
	 * @param db
	 *            The database.
	 */
	public void onCreate(SQLiteDatabase db)
	{
		String info_sql = Database.createSqlMap.getProperty("info.create");
		db.execSQL(info_sql);
		/*db
				.execSQL("CREATE TABLE IF NOT EXISTS [attchmentinfo] ([attchid] TEXT  UNIQUE NOT NULL PRIMARY KEY," 
						+
						"[mailuid] TEXT  NULL,[filesize] TEXT  NOT NULL,[filename] TEXT  NOT NULL,[content_uri] TEXT," 
						+
						"[mime_type] TEXT,[downloadfilename] TEXT  NULL,[preview] TEXT);");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [userinfo] ([accountid] TEXT UNIQUE NOT NULL PRIMARY KEY"
						+
						" ,[password] TEXT  NULL,[status] TEXT  NOT NULL,[username] TEXT  NULL ," 
						+
						"[savepassword] TEXT  NULL,"
						+
						"[aotulogin] TEXT  NULL,[offlinelogin] TEXT  NULL)");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [strategyinfo] ([id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
						+
						"[policykey] TEXT  NOT NULL,[policylist] TEXT  NOT NULL,[accountinfo] TEXT NOT NULL);");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [sentmessageinfo] " 
						+
						"([sentid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[mailuid] TEXT NULL,"
						+
						"[accountid] TEXT  NOT NULL,[mailsize] TEXT  NULL,[mailaccount] TEXT  NOT NULL,"
						+
						"[subject] TEXT  NULL,[mailbcc] TEXT  NULL,[mailto] TEXT  NOT NULL,[mailcc] TEXT  NULL," 
						+
						"[priority] INTEGER DEFAULT '1' NOT NULL,[readreply] INTEGER  NOT NULL," 
						+
						"[attchments]  TEXT  NULL,[mailbody] TEXT  NULL,[referenceid] TEXT  NULL," 
						+
						"[messageid] TEXT  NOT NULL,[issent] TEXT  NOT NULL, [flag] TEXT  NOT NULL," 
						+
						" [settime] TEXT NULL);");
		// :0已发送,1草稿箱
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [mailmessageinfo] ([mailuid] TEXT  UNIQUE NOT NULL PRIMARY KEY,"
						+
						"[mailaccount] TEXT  NOT NULL,[accountid] TEXT  NOT NULL,[mailsize] TEXT  NOT NULL," 
						+
						"[receivetime] FLOAT  NOT NULL,[subject] TEXT  NULL,[mailfrom] TEXT  NULL,[mailto] TEXT  NULL,"
						+
						"[mailcc] TEXT  NULL,[priority] INTEGER  NOT NULL,[readreply] INTEGER  NOT NULL," 
						+
						"[recvtype] INTEGER  NOT NULL,[mailbody] TEXT  NULL,[attachment_count] TEXT  NULL, " 
						+
						"[isread] TEXT  NOT NULL, [flag] TEXT  NOT NULL, [isdelete] TEXT NULL," 
						+
						" [ishasbody] TEXT NULL);");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [mailaccountinfo] " 
						+
						"([mailaccount] TEXT  PRIMARY KEY NOT NULL,[accountid] TEXT  NOT NULL," 
						+
						"[mailpassword] TEXT  NULL,[mailname] TEXT  NULL,[mailusername] TEXT  NULL," 
						+
						"[defaultfrom] BOOLEAN  NOT NULL,[recvserver] TEXT  NOT NULL,[recvserverport] TEXT  NOT NULL," 
						+
						"[recv_ssl] TEXT  NULL,[sentserver] TEXT  NULL,[sentserverport] TEXT   NULL,"
						+
						"[send_ssl] BOOLEAN   NULL,[mailsigntext] TEXT NULL);");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS [domaininfo] "
						+
						"([mailserver] TEXT  UNIQUE NOT NULL,[version] TEXT  NULL,[recvserver] TEXT  NOT NULL,"
						+
						"[recvtype] TEXT  NOT NULL,[recvport] TEXT  NOT NULL,[recv_ssl] TEXT  NULL," 
						+
						"[sendserver] TEXT  NOT NULL,[sendtype] TEXT  NULL,[sendport] TEXT  NOT NULL," 
						+
						"[send_ssl] TEXT  NULL,[queryinterval] TEXT  NULL,[smtp_auth] TEXT  NULL,"
						+
						"[fullauth] TEXT  NULL,[recvmailsize] TEXT  NULL,[proxy_useable] BOOLEAN  NOT NULL," 
						+
						"[proxy_protocol] TEXT  NULL,[proxy_addr] TEXT  NULL,[proxy_port] TEXT  NULL," 
						+
						"[proxy_user] TEXT  NULL,[proxy_passwd] TEXT  NULL);");

		db
				.execSQL("CREATE TABLE IF NOT EXISTS PreviewCaches" 
						+
						"(pagenum nvarchar(4),pagetotalnum nvarchar(4), path nvarchar(256)," 
						+
						" previewfilename nvarchar(100),updatetime numeric(16),md5 nvarchar(50)," 
						+
						"dirpath nvarchar(256),identifier nvarchar(256));");
		db.execSQL("CREATE TABLE IF NOT EXISTS ContactCaches(mailname nvarchar(256), mailaddress nvarchar(256));");*/
	}

	/**
	 * 
	 * @param db
	 *            SQLiteDatabase
	 */
	public void createContactCaches(SQLiteDatabase db)
	{
		try
		{
			if (db != null)
			{
				db
						.execSQL("CREATE TABLE IF NOT EXISTS ContactCaches(mailname nvarchar(256),"
								+
								" mailaddress nvarchar(256));");
			}
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	/**
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 * <p>
	 * The SQLite ALTER TABLE documentation can be found <a
	 * href="http://sqlite.org/lang_altertable.html">here</a>. If you add new
	 * columns you can use ALTER TABLE to insert them into a live table. If you
	 * rename or remove columns you can use ALTER TABLE to rename the old table,
	 * then create the new table and then populate the new table with the
	 * contents of the old table.
	 * 
	 * @param db
	 *            The database.
	 * @param oldVersion
	 *            The old database version.
	 * @param newVersion
	 *            The new database version.
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS attchmentinfo");
		db.execSQL("DROP TABLE IF EXISTS userinfo");
		db.execSQL("DROP TABLE IF EXISTS strategyinfo");
		db.execSQL("DROP TABLE IF EXISTS sentmessageinfo");
		db.execSQL("DROP TABLE IF EXISTS mailmessageinfo");
		db.execSQL("DROP TABLE IF EXISTS mailaccountinfo");
		db.execSQL("DROP TABLE IF EXISTS domaininfo");
		db.execSQL("DROP TABLE IF EXISTS PreviewCaches");
		db.execSQL("DROP TABLE IF EXISTS ContactCaches");
		onCreate(db);
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return tabIsExist
	 */
	public boolean tabIsExist(String tabName)
	{
		boolean result = false;
		if (tabName == null)
		{
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim()
					+ "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext())
			{
				int count = cursor.getInt(0);
				if (count > 0)
				{
					result = true;
				}
			}

		}
		catch (Exception e)
		{
			e.getMessage();
		}
		return result;
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param sqltab
	 *            sqltab
	 * @param tabName
	 *            表名
	 * @return tablIsExist
	 */
	public boolean tablIsExist(String sqltab, String tabName)
	{
		boolean result = false;
		if (sqltab == null)
		{
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = this.getReadableDatabase();
			String sql = "select sql from sqlite_master where type ='table' and name =?";
			cursor = db.rawQuery(sql, new String[]
			{
				tabName.trim()
			});
			if (cursor.moveToNext())
			{
				result = sqltab.equals(cursor.getString(0));
				android.util.Log.i("Database", cursor.getString(0));

			}

		}
		catch (Exception e)
		{
			e.getMessage();
		}
		return result;
	}

	/**
	 * Called when the database has been opened. Override method should check
	 * {@link SQLiteDatabase#isReadOnly} before updating the database.
	 * 
	 * @param db
	 *            The database.
	 */
	public void onOpen(SQLiteDatabase db)
	{
	}
}
