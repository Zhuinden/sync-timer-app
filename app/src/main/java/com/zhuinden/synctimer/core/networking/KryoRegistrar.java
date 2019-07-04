/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.core.networking;

import com.esotericsoftware.kryo.Kryo;
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand;
import com.zhuinden.synctimer.core.networking.commands.StartSessionCommand;
import com.zhuinden.synctimer.core.networking.commands.StopTimerByUserCommand;
import com.zhuinden.synctimer.core.networking.commands.TimerSyncCommand;

public class KryoRegistrar {
    private KryoRegistrar() {
    }

    public static void configureKryo(Kryo kryo) {
        kryo.register(float[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(String[].class);
        kryo.register(JoinSessionCommand.class);
        kryo.register(StartSessionCommand.class);
        kryo.register(StopTimerByUserCommand.class);
        kryo.register(TimerSyncCommand.class);
    }
}
