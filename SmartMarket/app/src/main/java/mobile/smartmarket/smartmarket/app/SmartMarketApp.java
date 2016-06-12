package mobile.smartmarket.smartmarket.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.atomic.AtomicInteger;

import mobile.smartmarket.smartmarket.helpers.Constants;
import mobile.smartmarket.smartmarket.helpers.GCMRegister;
import mobile.smartmarket.smartmarket.helpers.PreferenceManagerImp;

/**
 * Created by omar on 11/6/16.
 */
public class SmartMarketApp extends Application{
    private static final String LOG_TAG = Constants.STR_LOG_TAG.concat("SmartMarketApp");
    private static final boolean IS_DEBBUG = Constants.isDebbud;

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;

    public void onCreate () {
        super.onCreate();
        if(IS_DEBBUG) Log.d(LOG_TAG, "Inicio");

        if (checkPlayServices()) {
            regid = getRegistrationId();
            if (regid.isEmpty()) {
                try {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    new GCMRegister(getApplicationContext(), gcm, getAppVersion(), getAppVersionName()).execute();
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.d(LOG_TAG, "GooglePlayServicesAvailable not found. "+ String.valueOf(resultCode));
            return false;
        }
        Log.d(LOG_TAG, "GooglePlayServicesAvailable found.");
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId() {
        String registrationId = PreferenceManagerImp.getInstance(this).getPreferenceValue(Constants.STR_PREFERENCES_01);
        if (registrationId.equalsIgnoreCase(Constants.STR_NOTAVAILABLE)) {
            Log.d(LOG_TAG, "Registration not found.");
            return Constants.STR_EMPTY;
        }

        int registeredVersion = PreferenceManagerImp.getInstance(this).getPreferenceValueInt(Constants.STR_PREFERENCES_02);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.d(LOG_TAG, "App version changed.");
            return Constants.STR_EMPTY;
        }

        String registeredVersionName = PreferenceManagerImp.getInstance(this).getPreferenceValue(Constants.STR_PREFERENCES_03);
        String currentVersionName = getAppVersionName();
        if (registeredVersionName.equalsIgnoreCase(currentVersionName)) {
            Log.d(LOG_TAG, "App versionName changed.");
            return Constants.STR_EMPTY;
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            return 0;
        }
    }

    /**
     * @return Application's version name from the {@code PackageManager}.
     */
    private String getAppVersionName() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            return Constants.STR_EMPTY;
        }
    }
}
