/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.core.networking.commands;

import com.zhuinden.synctimer.core.timer.TimerConfiguration;

;

public class StartSessionCommand {
    public int startValue;
    public int endValue;
    public int decreaseStep;
    public int decreaseInterval;

    public StartSessionCommand() {
    }

    public StartSessionCommand(TimerConfiguration timerConfiguration) {
        this.startValue = timerConfiguration.getStartValue();
        this.endValue = timerConfiguration.getEndValue();
        this.decreaseStep = timerConfiguration.getDecreaseStep();
        this.decreaseInterval = timerConfiguration.getDecreaseInterval();
    }

    public TimerConfiguration getTimerConfiguration() {
        return new TimerConfiguration(startValue, endValue, decreaseStep, decreaseInterval);
    }
}
