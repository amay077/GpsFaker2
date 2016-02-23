package net.amay077.gpsfaker2.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by PC081N on 2016/02/23.
 */
public class GpsSignalService extends IntentService {

    private static final String PARAM_SOURCE_NAME = "source_name";
    private static final String TAG = "GpsSignalService";

    public static Intent newIntent(Context context, String source) {
        final Intent intent = new Intent(context, GpsSignalService.class);
        intent.putExtra(PARAM_SOURCE_NAME, source);

        return intent;
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GpsSignalService(String name) {
        super(name);
        Log.d(TAG, "ctor(" + name + ")");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

//    public GpsSignalService() {
//        super("GpsSignalService");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.d(TAG, "onHandleIntent");
//
//        for (int i = 0; i < 5; i++) {
//            try {
//                Thread.sleep(1000);
//                Log.d(TAG, "some job:" + i);
//            } catch (InterruptedException e) { }
//        }
//
//        Log.d(TAG, "job completed.");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy");
//    }
}
