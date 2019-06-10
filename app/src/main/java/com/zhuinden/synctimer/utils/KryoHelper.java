package com.zhuinden.synctimer.utils;

import com.esotericsoftware.kryo.Kryo;
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand;

public class KryoHelper {
    private KryoHelper() {
    }

    public static void configureKryo(Kryo kryo) {
        kryo.register(float[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(String[].class);
        kryo.register(JoinSessionCommand.class);
    }
}
