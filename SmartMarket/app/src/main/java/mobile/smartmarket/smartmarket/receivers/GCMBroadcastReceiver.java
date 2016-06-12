package mobile.smartmarket.smartmarket.receivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import mobile.smartmarket.smartmarket.services.GCMIntentService;


/**
 * Created by omar on 11/6/16.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());

        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
    }
}
