package net.amay077.gpsfaker2.viewmodels;

import android.app.Activity;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import net.amay077.gpsfaker2.App;
import net.amay077.gpsfaker2.models.GpsSignalModel;

import java.lang.ref.WeakReference;

/**
 * Created by PC081N on 2016/02/23.
 */
public class MainViewModel {

    private static final String TAG = "MainViewModel";
    private final WeakReference<Activity> weakActivity;

    public final ObservableField<String> selectedProvider = new ObservableField<>("");

    public MainViewModel(Activity activity) {
        weakActivity = new WeakReference<>(activity);
    }

    public void onClickPlay(View view) {
        Log.d(TAG, "onClickPlay");
        getGpsModel().start(selectedProvider.get());
    }

    public void onClickStop(View view) {
        Log.d(TAG, "onClickStop");
        getGpsModel().stop();
    }

    private GpsSignalModel getGpsModel() {
        return ((App)weakActivity.get().getApplication()).getGpsSignalModel();
    }
}
