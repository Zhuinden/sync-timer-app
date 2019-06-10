/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
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
