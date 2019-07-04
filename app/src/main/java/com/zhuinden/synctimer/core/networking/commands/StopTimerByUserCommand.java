/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.core.networking.commands;

public class StopTimerByUserCommand {
    public String username;

    public StopTimerByUserCommand() {
    }

    public StopTimerByUserCommand(String username) {
        this.username = username;
    }
}
