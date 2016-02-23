package net.amay077.gpsfaker2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.amay077.gpsfaker2.R;
import net.amay077.gpsfaker2.models.RunningStatus;

/**
 * Created by PC081N on 2016/02/23.
 */
public class MyPrefs {
    private static String TAG = "MyPrefs";

    private static final String KEY_RUNNING_STATUS = "running_status";
    private static final String KEY_STOP_REQUEST = "stop_request";

    static private MyPrefs instance = null;
    private final SharedPreferences pref;

    static public MyPrefs getInstance(Context context) {
        if (instance != null) return instance;

        instance = new MyPrefs(context);
        return instance;
    }

    private MyPrefs(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setRunningStatus(RunningStatus value) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(KEY_RUNNING_STATUS, value.value);
        edit.commit();
    }

    public RunningStatus getRunningStatus() {
        return RunningStatus.fromValue(pref.getInt(KEY_RUNNING_STATUS, 0));
    }

    public void setStopRequest(boolean wantStop) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(KEY_STOP_REQUEST, wantStop);
        edit.commit();
    }

    public boolean isStopRequest() {
        return pref.getBoolean(KEY_STOP_REQUEST, false);
    }
}
