package mobile.smartmarket.smartmarket.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by omar on 11/6/16.
 */
public class PreferenceManagerImp {
    private static PreferenceManagerImp ourInstance = null;
    private static SharedPreferences preferences = null;

    public static synchronized PreferenceManagerImp getInstance(Context context) {
        if (ourInstance==null){
            ourInstance = new PreferenceManagerImp(context);
        }
        return ourInstance;
    }

    private PreferenceManagerImp() {
    }

    private PreferenceManagerImp(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getPreferenceValue(String theKey) {
        return preferences.getString(theKey, Constants.STR_NOTAVAILABLE);
    }

    public float getPreferenceValueFloat(String theKey) {
        return preferences.getFloat(theKey, Float.MIN_VALUE);
    }

    public long getPreferenceValueLong(String theKey) {
        return preferences.getLong(theKey, Long.MIN_VALUE);
    }

    public int getPreferenceValueInt(String theKey) {
        return preferences.getInt(theKey, Integer.MIN_VALUE);
    }

    public boolean getPreferenceValueBoolean(String theKey) {
        return preferences.getBoolean(theKey, false);
    }

    public void updatePreferenceValue(String theKey, String theValue) {
        Editor edit = preferences.edit();
        edit.putString(theKey, theValue);
        edit.apply();
    }

    public void updatePreferenceValueFloat(String theKey, float theValue) {
        Editor edit = preferences.edit();
        edit.putFloat(theKey, theValue);
        edit.apply();
    }

    public void updatePreferenceValueLong(String theKey, long theValue) {
        Editor edit = preferences.edit();
        edit.putLong(theKey, theValue);
        edit.apply();
    }

    public void updatePreferenceValueInt(String theKey, int theValue) {
        Editor edit = preferences.edit();
        edit.putInt(theKey, theValue);
        edit.apply();
    }

    public void updatePreferenceValueBoolean(String theKey, boolean theValue) {
        Editor edit = preferences.edit();
        edit.putBoolean(theKey, theValue);
        edit.apply();
    }

}
