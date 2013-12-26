package com.codingPower.manager.autoupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.codingPower.R;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class AutoUpdateManager {
	public static final String TAG = "AutoUpdateManager";
	public static final String VERSION = "VERSION";
	private static AutoUpdateManager manager;

	private CheckVersionJSONAdapter checkVersionJSONAdapter;
	private FileUrlAdapter fileUrlAdapter;
	private OFNotificationHelper mNotificationHelper;
	private OnConfirmDialogShowListener mOnConfirmDialogShowListener;

	private Activity mAct;
	private CheckUpdateTask mCheckUpdateTask;
	private DownloadTask mDownloadTask;

	private int mVersionCode;
	private String mVersionName;

	public static AutoUpdateManager getInstance() {
		if (manager == null) {
			manager = new AutoUpdateManager();
		}
		return manager;
	}

	public AutoUpdateManager checkUpdate(Activity act, String url) {
		mAct = act;
		mNotificationHelper = new OFNotificationHelper(act, R.string.app_name);
		try {
			PackageInfo pInfo = act.getPackageManager().getPackageInfo(
					act.getPackageName(), 0);
			mVersionCode = pInfo.versionCode;
			mVersionName = pInfo.versionName;
			mCheckUpdateTask = new CheckUpdateTask();
			mCheckUpdateTask.execute(url);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}

	public AutoUpdateManager setCheckVersionJSONAdapter(
			CheckVersionJSONAdapter adapter) {
		checkVersionJSONAdapter = adapter;
		return this;
	}

	public AutoUpdateManager setFileUrlAdapter(FileUrlAdapter adapter) {
		fileUrlAdapter = adapter;
		return this;
	}

	public AutoUpdateManager setOnConfirmDialogShowListener(
			OnConfirmDialogShowListener listener) {
		mOnConfirmDialogShowListener = listener;
		return this;
	}

	/**
	 * 监听确认对话框弹出监听器
	 * 
	 * @author pengf
	 * 
	 */
	public static interface OnConfirmDialogShowListener {
		/**
		 * 监听确认对话框弹出
		 * 
		 * @author pengf
		 * 
		 */
		void onShow(CheckUpdateResult res);
	}

	public static interface FileUrlAdapter {
		/**
		 * 请求链接
		 * 
		 * @param version
		 * @return
		 */
		public String fileUrl(String version);
	}

	public static interface CheckVersionJSONAdapter {

		/**
		 * 检查版本升级的请求参数拼接
		 * 
		 * @param versionCode
		 * @param versionName
		 * @return
		 */
		public String checkVersionParam(int versionCode, String versionName);

		/**
		 * 处理升级请求的响应
		 * 
		 * @param json
		 *            返回结果JSON对象
		 * @return CheckUpdateResult 结果对象
		 */
		public CheckUpdateResult needUpdate(JSONObject json);

		/**
		 * 升级请求取消的后续处理，发生在点击取消事件和无需升级的情况
		 */
		public void next();

	}

	public static CheckUpdateResult createCheckUpdateResult(
			String updateVersion, String msg, boolean needUpdate) {
		CheckUpdateResult res = new CheckUpdateResult();
		res.updateVersion = updateVersion;
		res.msg = msg;
		res.needUpdate = needUpdate;
		return res;
	}

	public static class CheckUpdateResult {
		private CheckUpdateResult() {
		};

		private String updateVersion;
		private String msg;
		private boolean needUpdate;

		public String toString() {
			return updateVersion + "," + msg + "," + needUpdate;
		}
	}

	private class CheckUpdateTask extends
			AsyncTask<String, Void, CheckUpdateResult> {

		@Override
		protected CheckUpdateResult doInBackground(String... urls) {
			Log.i(TAG, "url:" + urls[0]);
			if (checkVersionJSONAdapter == null)
				return null;
			try {
				String param = checkVersionJSONAdapter.checkVersionParam(
						mVersionCode, mVersionName);
				Log.i(TAG, "param:" + param);
				String rsp = HttpRequest
						.post(urls[0])
						.contentType(HttpRequest.CONTENT_TYPE_JSON)
						.send(param)
						.body();
				Log.i(TAG, "rsp:" + rsp);
				JSONObject obj = new JSONObject(rsp);
				return checkVersionJSONAdapter.needUpdate(obj);
			} catch (HttpRequestException e1) {
				e1.printStackTrace();
				return null;
			} catch (JSONException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(final CheckUpdateResult res) {
			Log.i(TAG, "CheckUpdateResult:" + res);
			if (checkVersionJSONAdapter == null)
				return;
			if (res == null || !res.needUpdate
					|| isIgnoreVersion(res.updateVersion)) {
				checkVersionJSONAdapter.next();
			} else if (mAct != null && !mAct.isFinishing()) {
				//TODO:dialog theme问题
				AlertDialog.Builder builder = new AlertDialog.Builder(mAct,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.download_update_title)
						.setCancelable(false)
						.setPositiveButton(R.string.update,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mDownloadTask = new DownloadTask();
										mDownloadTask.execute(fileUrlAdapter
												.fileUrl(res.updateVersion));
										checkVersionJSONAdapter.next();
									}
								})
						.setNegativeButton(R.string.cancel,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										checkVersionJSONAdapter.next();
									}
								})
						.setNeutralButton(R.string.update_ignore,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										saveIgnoreVersion(res.updateVersion);
										checkVersionJSONAdapter.next();
									}
								});
				if (!"".equals(res.msg)) {
					builder.setMessage(res.msg);
				}
				builder.create().show();
				if(mOnConfirmDialogShowListener != null)
					mOnConfirmDialogShowListener.onShow(res);
			}
		}
	}

	/**
	 * 检查获取的版本号是否是之前跳过的版本
	 * 
	 * @param res
	 * @return
	 */
	private boolean isIgnoreVersion(String version) {
		String ignoreVersion = mAct
				.getApplicationContext()
				.getSharedPreferences(TAG, 0)
				.getString(VERSION, null);
		return version.equals(ignoreVersion);
	}

	/**
	 * 保存需要跳过的版本号
	 * 
	 * @param version
	 */
	private void saveIgnoreVersion(String version) {
		mAct.getApplicationContext()
				.getSharedPreferences(TAG, 0)
				.edit()
				.putString(VERSION, version)
				.commit();
	}

	private class DownloadTask extends AsyncTask<String, Integer, File> {
		long size = 0;
		int lastPercent = -1;

		protected File doInBackground(String... urls) {
			Log.i(TAG, "DownloadTask url:" + urls[0]);
			try {
				HttpRequest request = HttpRequest.get(urls[0]);
				File file = null;
				if (request.ok()) {
					//File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
					//file = new File(dataDir, "download.tmp");
					file = new File(
							getCacheDirectory(mAct.getApplicationContext()),
							"download.tmp");
					if (file.exists())
						file.delete();
					//file = File.createTempFile("download", ".tmp");
					final long total = request.contentLength();
					Log.i(TAG, "receive size:" + total);
					Log.i(TAG, "receive contentType:" + request.contentType());
					request.receive(new FileOutputStream(file) {
						@Override
						public void write(byte[] buffer, int byteOffset,
								int byteCount) throws IOException {
							super.write(buffer, byteOffset, byteCount);
							size += byteCount;
							int cPercent = (int) (size * 100 / total);
							if (cPercent != lastPercent) {
								publishProgress(cPercent);
								lastPercent = cPercent;
							}
						}
					});
				}
				return file;
			} catch (HttpRequestException e) {
				mNotificationHelper.completed();
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				mNotificationHelper.completed();
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mNotificationHelper.createNotification();
		}

		protected void onProgressUpdate(Integer... progress) {
			mNotificationHelper.progressUpdate(progress[0]);
			Log.d("MyApp", "Downloaded bytes: " + progress[0]);
		}

		protected void onPostExecute(File file) {
			mNotificationHelper.completed();
			if (file != null) {
				Log.d("MyApp", "Downloaded file to: " + file.getAbsolutePath());
				update(mAct.getApplicationContext(), file);
			} else
				Log.d("MyApp", "Download failed");
		}
	}

	/**
	 * @Description: 获取SD卡缓存目录
	 * @param context
	 */
	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context, "cache");
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context, String file) {
		File dataDir = new File(new File(
				Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(
				new File(dataDir, context.getPackageName()), file);
		if (!appCacheDir.exists()) {
			try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log
						.e(TAG,
								"Can't create \".nomedia\" file in application external cache directory");
			}
			if (!appCacheDir.mkdirs()) {
				Log.w(TAG, "Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}
	
	private static void update(Context context, File apk) {
		if(!apk.exists())
			return;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setComponent(new ComponentName("com.android.packageinstaller",
				"com.android.packageinstaller.PackageInstallerActivity"));
		intent.setDataAndType(Uri.fromFile(apk),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
		}
	}

}
