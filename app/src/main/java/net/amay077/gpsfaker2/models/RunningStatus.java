package net.amay077.gpsfaker2.models;

/**
 * Created by PC081N on 2016/02/23.
 */
public enum RunningStatus {
    NONE(0),
    START(1),
    PAUSE(2),
    STOP(3);

    public final int value;

    RunningStatus(int value) {
        this.value = value;
    }

    public static RunningStatus fromValue(int value) {
        for (RunningStatus v : RunningStatus.values()) {
            if (v.value == value) {
                return v;
            }
        }

        return RunningStatus.NONE;
    }
}
