/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.core.networking.commands;

public class JoinSessionCommand {
    public JoinSessionCommand() {
    }

    public JoinSessionCommand(String username) {
        this.username = username;
    }

    public String username;
}
