package net.amay077.gpsfaker2;

import android.app.Application;

import net.amay077.gpsfaker2.models.GpsSignalModel;

/**
 * Created by PC081N on 2016/02/23.
 */
public class App extends Application {

    private GpsSignalModel gpsSignalModel;

    @Override
    public void onCreate() {
        super.onCreate();

        gpsSignalModel = new GpsSignalModel(this);
    }

    public GpsSignalModel getGpsSignalModel() {
        return gpsSignalModel;
    }
}
