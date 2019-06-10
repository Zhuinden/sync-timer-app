package com.zhuinden.synctimer.core.networking;

import com.esotericsoftware.kryo.Kryo;
import com.zhuinden.synctimer.core.networking.commands.*;

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
        kryo.register(StartTimerByHostCommand.class);
        kryo.register(StopTimerByUserCommand.class);
        kryo.register(SyncTimerCommand.class);
    }
}
