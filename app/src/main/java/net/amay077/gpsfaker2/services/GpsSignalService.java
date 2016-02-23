package net.amay077.gpsfaker2.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.ddmuilib.location.GpxParser;
import com.android.ddmuilib.location.TrackPoint;

import net.amay077.gpsfaker2.R;
import net.amay077.gpsfaker2.models.RunningStatus;
import net.amay077.gpsfaker2.utils.MyPrefs;
import net.amay077.gpsfaker2.views.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by PC081N on 2016/02/23.
 */
public class GpsSignalService extends IntentService {

    private static final String PARAM_SOURCE_NAME = "source_name";
    private static final String TAG = "GpsSignalService";
    private static final String PROVIDER_NAME = LocationManager.GPS_PROVIDER; //"GpsFaker";
    private static final int REQUEST_CODE_TEST = 1;
    private static final int NOTIFICATION_TEXT_ID = 2;

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

    public GpsSignalService() {
        super("GpsSignalService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        final MyPrefs pref = MyPrefs.getInstance(this);
        pref.setRunningStatus(RunningStatus.START);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);

        final Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), REQUEST_CODE_TEST, mainIntent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(getResources().getString(R.string.app_name)) // 1行目
                .setContentText("Running..") // 2行目
                .setWhen(System.currentTimeMillis()) // タイムスタンプ
                .setOngoing(true)
                .setContentIntent(contentIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(NOTIFICATION_TEXT_ID, builder.build());

        final String source = intent.getStringExtra(PARAM_SOURCE_NAME);
        final List<Location> locations = makeGpsDataFromGpx(this, source);

        final LocationManager locMan = (LocationManager)this
                .getSystemService(Context.LOCATION_SERVICE);
        enableTestProvider(locMan);

        int i = 0;
        while (i < locations.size()) {
            try {
                if (pref.isStopRequest()) {
                    Log.d(TAG, "abort job.");
                    pref.setStopRequest(false);
                    break;
                }

                if (pref.getRunningStatus() == RunningStatus.PAUSE) {
                    Thread.sleep(1000);
                    continue;
                }

                final Location location = locations.get(i);
                location.setTime(System.currentTimeMillis());
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                locMan.setTestProviderLocation(PROVIDER_NAME, location);
                Log.v(TAG, "Provide test location. - " + location);

                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e(TAG, "Provide test location failed.", e);
            }

            i++;
        }

        manager.cancel(NOTIFICATION_TEXT_ID);
        pref.setRunningStatus(RunningStatus.STOP);
        Log.d(TAG, "job completed.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void enableTestProvider(LocationManager locMan) {
        locMan.addTestProvider(PROVIDER_NAME,
                false, // requiresNetwork,
                true, // requiresSatellite,
                false, // requiresCell,
                false, // hasMonetaryCost,
                true, // supportsAltitude,
                true, // supportsSpeed,
                false, // supportsBearing,
                0, // powerRequirement,
                1); // accuracy)

        // ここで onProviderEnabled/Disabled が出るはず
        locMan.setTestProviderEnabled(PROVIDER_NAME, true);
    }

    private List<Location> makeGpsDataFromGpx(Context context, String gpxPath) throws NoSuchElementException {
        List<Location> locations = new ArrayList<Location>();

        AssetManager assetManager = context.getResources().getAssets();
        InputStream input;
        try {
            input = assetManager.open(gpxPath);
        } catch (IOException e) {
            Log.e(TAG, "asset open failed.", e);
            return locations;
        }

        GpxParser parser = new GpxParser(new InputStreamReader(input));

        if (!parser.parse()) {
            throw new NoSuchElementException("GpxParser.parse failed.");
        }
        GpxParser.Track[] tracks = parser.getTracks();
        if (tracks == null || tracks.length <= 0) {
            return locations;
        }

        final long now = System.currentTimeMillis();
        Long prev = null;
        for (TrackPoint point : tracks[0].getPoints()) {
            Location location = new Location(PROVIDER_NAME);
            location.setLatitude(point.getLatitude());
            location.setLongitude(point.getLongitude());
            location.setAccuracy(10f);
            location.setAltitude(point.getElevation());
            if (prev == null) {
                location.setTime(now);
            } else {
                location.setTime(now + (point.getTime() - prev));
            }
            prev = point.getTime();
// NOTE ignore			location.setTime(point.getTime());
            locations.add(location);
        }

        return locations;
    }
}
