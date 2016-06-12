package mobile.smartmarket.smartmarket.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import mobile.smartmarket.smartmarket.R;
import mobile.smartmarket.smartmarket.activities.PushActivity;
import mobile.smartmarket.smartmarket.helpers.Constants;
import mobile.smartmarket.smartmarket.helpers.PreferenceManagerImp;
import mobile.smartmarket.smartmarket.receivers.GCMBroadcastReceiver;

/**
 * Created by omar on 11/6/16.
 */
public class GCMIntentService extends IntentService {
    private static final String LOG_TAG = Constants.STR_LOG_TAG.concat("GCMIntentService");
    private static final boolean IS_DEBBUG = Constants.isDebbud;
    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*
                for (int i=0; i<5; i++) {
                    if(IS_DEBBUG) Log.d(LOG_TAG, "Working... " + (i+1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                if(IS_DEBBUG) Log.d(LOG_TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                type
                	0 Ninguna
                	1 Solo vibra
                	2 Solo suena
                	3 Ambos
                */
                if (IS_DEBBUG) Log.d(LOG_TAG, "message:    " + extras.getString("message"));
                if (IS_DEBBUG) Log.d(LOG_TAG, "title:      " + extras.getString("title"));
                if (IS_DEBBUG) Log.d(LOG_TAG, "idcard:     " + extras.getString("idcard"));
                if (IS_DEBBUG) Log.d(LOG_TAG, "vendedor: " + extras.getString("vendedor"));
                if (IS_DEBBUG) Log.d(LOG_TAG, "monto:  " + extras.getString("monto"));

                sendNotification(
                        extras.getString("message"),
                        extras.getString("title"),
                        extras.getString("idcard"),
                        extras.getString("vendedor"),
                        extras.getString("monto")
                );
                if (IS_DEBBUG) Log.d(LOG_TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String tit, String idcard, String vendedor, String monto) {
        if (idcard != null) {
            if (IS_DEBBUG) Log.d(LOG_TAG, "tit:   " + tit);
            if (IS_DEBBUG) Log.d(LOG_TAG, "idcard:     " + idcard);
            if (IS_DEBBUG) Log.d(LOG_TAG, "vendedor: " + vendedor);
            if (IS_DEBBUG) Log.d(LOG_TAG, "monto:   " + monto);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(getApplicationContext(), PushActivity.class);
            intent.putExtra("message", msg);
            intent.putExtra("title", tit);
            intent.putExtra("idcard", idcard);
            intent.putExtra("vendedor", vendedor);
            intent.putExtra("monto", monto);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), Constants.INT_REQUESTCODE_MAIN, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_alert)
                    .setContentTitle(tit)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setLights(Color.CYAN, 5000, 5000);

            //if (type.equals(Constants.STR_ONE) || type.equals(Constants.STR_THREE))
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

            //if ((type.equals(Constants.STR_TWO) || type.equals(Constants.STR_THREE)) && (alarmSound != null))
            mBuilder.setSound(alarmSound);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    public static boolean isMainActivityIsOpen(Context context) {
        if (IS_DEBBUG) {
            StringBuffer msgLog = new StringBuffer();
            msgLog.append("isMainActivityIsOpen: ");
            msgLog.append(PreferenceManagerImp.getInstance(context).getPreferenceValueBoolean(Constants.STR_PREFERENCES_05));
            Log.d(LOG_TAG, msgLog.toString());
        }

        return PreferenceManagerImp.getInstance(context).getPreferenceValueBoolean(Constants.STR_PREFERENCES_05);
    }
}
