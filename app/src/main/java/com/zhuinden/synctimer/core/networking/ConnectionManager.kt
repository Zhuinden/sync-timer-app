package com.zhuinden.synctimer.core.networking

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Server
import com.zhuinden.synctimer.utils.register

class ConnectionManager {
    private fun createServer() {
        val server: Server? = null
        val client: Client? = null
        client!!.discoverHost()
    }


    private fun configureKryo(kryo: Kryo) {
        kryo.register<FloatArray>()
        kryo.register<IntArray>()
        kryo.register<LongArray>()
        kryo.register<Array<String>>()
    }
}