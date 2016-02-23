package net.amay077.gpsfaker2.models;

import android.content.Context;
import android.util.Log;

import net.amay077.gpsfaker2.services.GpsSignalService;
import net.amay077.gpsfaker2.utils.MyPrefs;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by PC081N on 2016/02/23.
 */
public class GpsSignalModel implements Subscription {
    private static final String TAG = "GpsSignalModel";
    private final Context appContext;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final BehaviorSubject<RunningStatus> runningStatusBehavior = BehaviorSubject.<RunningStatus>create(RunningStatus.NONE); // タイマー時間
    private final SerializedSubject<RunningStatus, RunningStatus> runningStatusSerialized = new SerializedSubject<RunningStatus, RunningStatus>(runningStatusBehavior);

    public final Observable<RunningStatus> runningStatus = runningStatusBehavior;
    private final MyPrefs pref;

    public GpsSignalModel(Context appContext) {
        this.appContext = appContext;
        pref = MyPrefs.getInstance(appContext);
        subscriptions.add(Observable.interval(500, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, RunningStatus>() {
                    @Override
                    public RunningStatus call(Long dummy) {
                        return pref.getRunningStatus();
                    }
                })
                .distinctUntilChanged()
                .subscribe(new Action1<RunningStatus>() {
                    @Override
                    public void call(RunningStatus runningStatus) {
                        Log.d(TAG, "runningStatus changed : " + runningStatus);
                        runningStatusSerialized.onNext(runningStatus);
                    }
                }));
    }

    public void start(String source) {
        if (pref.getRunningStatus() == RunningStatus.PAUSE) {
            pref.setRunningStatus(RunningStatus.START);
        } else {
            appContext.startService(GpsSignalService.newIntent(appContext, source));
        }
    }

    public void pause() {
        pref.setRunningStatus(RunningStatus.PAUSE);
    }

    public void stop() {
        pref.setStopRequest(true);
        pref.setRunningStatus(RunningStatus.STOP);
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
