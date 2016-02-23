package net.amay077.gpsfaker2.models;

import android.content.Context;
import android.content.Intent;

import net.amay077.gpsfaker2.App;
import net.amay077.gpsfaker2.services.GpsSignalService;

/**
 * Created by PC081N on 2016/02/23.
 */
public class GpsSignalModel {
    private final Context appContext;

    public GpsSignalModel(Context appContext) {
        this.appContext = appContext;
    }

    public void start(String source) {
        appContext.startService(GpsSignalService.newIntent(appContext, source));
    }

    public void stop() {

    }
}
