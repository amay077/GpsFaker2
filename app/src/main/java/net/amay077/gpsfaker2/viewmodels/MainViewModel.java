package net.amay077.gpsfaker2.viewmodels;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import net.amay077.gpsfaker2.App;
import net.amay077.gpsfaker2.models.GpsSignalModel;
import net.amay077.gpsfaker2.models.RunningStatus;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by PC081N on 2016/02/23.
 */
public class MainViewModel implements Subscription {

    private static final String TAG = "MainViewModel";
    private final WeakReference<Activity> weakActivity;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public final ObservableField<String> selectedProvider = new ObservableField<>("");
    public final ObservableField<String> selectedSource = new ObservableField<>("");

    public final ObservableBoolean canStart = new ObservableBoolean(false);
    public final ObservableBoolean canPause = new ObservableBoolean(false);
    public final ObservableBoolean canStop = new ObservableBoolean(false);

    public MainViewModel(Activity activity) {
        weakActivity = new WeakReference<>(activity);

        subscriptions.add(getGpsModel().runningStatus.subscribe(new Action1<RunningStatus>() {
            @Override
            public void call(RunningStatus status) {
                canStart.set(status != RunningStatus.START);
                canPause.set(status == RunningStatus.START);
                canStop.set(status == RunningStatus.START || status == RunningStatus.PAUSE);
            }
        }));
    }

    public void onClickPlay(View view) {
        Log.d(TAG, "onClickPlay");
        canStart.set(false); // 連打禁止(なんかダサい)
        getGpsModel().start(selectedSource.get());
    }

    public void onClickPause(View view) {
        Log.d(TAG, "onClickPause");
        canPause.set(false); // 連打禁止(なんかダサい)
        getGpsModel().pause();
    }

    public void onClickStop(View view) {
        Log.d(TAG, "onClickStop");
        canStop.set(false); // 連打禁止(なんかダサい)
        getGpsModel().stop();
    }

    private GpsSignalModel getGpsModel() {
        return ((App)weakActivity.get().getApplication()).getGpsSignalModel();
    }

    @Override
    public void unsubscribe() {
        subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
