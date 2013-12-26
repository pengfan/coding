package com.codingPower.manager.autoupdate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.codingPower.R;


/**
 * TODO
 * 
 * @author wangwenbin 2013-1-27
 */
public class OFNotificationHelper {
	private String mTitle;
	private Context mContext;
	private int NOTIFICATION_ID = 1;
	private int NOTIFICATION_ERROR_ID = 2;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private PendingIntent mContentIntent;

	public OFNotificationHelper(Context context, int titleResId) {
		mContext = context;
		mTitle = context.getString(titleResId);
	}

	/**
	 * Put the notification into the status bar
	 */
	public void createNotification() {
		// get the notification manager
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// create the notification
		int icon = android.R.drawable.stat_sys_download;
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, mContext.getText(R.string.downloading), when);
		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.of_download_notify);
		contentView.setTextViewText(R.id.notify_title, mTitle);
		contentView.setTextViewText(R.id.notify_state, mContext.getString(R.string.downloadingfile));
		contentView.setProgressBar(R.id.notify_processbar, 100, 0, false);

		Intent notificationIntent = new Intent();
		mContentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, 0);
		mNotification.contentView = contentView;
		mNotification.contentIntent = mContentIntent;
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void progressUpdate(int percentageComplete) {
		mNotification.contentView.setTextViewText(R.id.notify_state, mContext.getString(R.string.downloaded)
				+ percentageComplete + "%");
		mNotification.contentView.setProgressBar(R.id.notify_processbar, 100,
				percentageComplete, false);
		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void completed() {
		// remove the notification from the status bar
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
	
	public void showError(){
		mNotificationManager.cancel(NOTIFICATION_ID);
		String failueHint = mContext.getString(R.string.download_failue);
		mNotification.contentView.setTextViewText(R.id.notify_state, failueHint);
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(NOTIFICATION_ERROR_ID, mNotification);
	}
}
