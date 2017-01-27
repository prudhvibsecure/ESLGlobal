package eslglobal.com.esl.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import eslglobal.com.esl.LoginInWithPin;
import eslglobal.com.esl.R;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String msg = intent.getStringExtra("message");

				sendNotification(msg);

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		int numMessages = 0;
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LoginInWithPin.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher);
		try {
			JSONObject json = new JSONObject(msg);
			mBuilder.setContentTitle(json.optString("title"))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(json.optString("msg")))
					.setContentText(json.optString("msg"));
		} catch (Exception e) {
			mBuilder.setContentTitle("ESLGlobal").setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
					.setContentText(msg).setNumber(++numMessages);
		}
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
		// mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

	}
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
//		if(preference.getPushStatus().equals("ON"))
			Log.d("onMessage", arg1.getExtras().getString("message"));

	}

}
