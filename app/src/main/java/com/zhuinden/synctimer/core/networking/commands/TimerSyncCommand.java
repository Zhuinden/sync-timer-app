package com.zhuinden.synctimer.core.networking.commands;

public class TimerSyncCommand {
    public int currentTime;
    public boolean isCountdownActive;
    public boolean isStoppedByUser;
    public String stoppedBy;
    public boolean isPaused;
    public boolean isTimerReachedEnd;

    public TimerSyncCommand() {
    }

    public TimerSyncCommand(int currentTime, boolean isCountdownActive, boolean isStoppedByUser, String stoppedBy, boolean isPaused, boolean isTimerReachedEnd) {
        this.currentTime = currentTime;
        this.isCountdownActive = isCountdownActive;
        this.isStoppedByUser = isStoppedByUser;
        this.stoppedBy = stoppedBy;
        this.isPaused = isPaused;
        this.isTimerReachedEnd = isTimerReachedEnd;
    }
}
