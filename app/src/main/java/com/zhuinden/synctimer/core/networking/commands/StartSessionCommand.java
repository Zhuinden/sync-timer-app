package com.zhuinden.synctimer.core.networking.commands;

import com.zhuinden.synctimer.features.serverlobby.ServerLobbyManager;

public class StartSessionCommand {
    public int startValue;
    public int endValue;
    public int decreaseStep;
    public int decreaseInterval;

    public StartSessionCommand() {
    }

    public StartSessionCommand(ServerLobbyManager.TimerConfiguration timerConfiguration) {
        this.startValue = timerConfiguration.getStartValue();
        this.endValue = timerConfiguration.getEndValue();
        this.decreaseStep = timerConfiguration.getDecreaseStep();
        this.decreaseInterval = timerConfiguration.getDecreaseInterval();
    }
}
