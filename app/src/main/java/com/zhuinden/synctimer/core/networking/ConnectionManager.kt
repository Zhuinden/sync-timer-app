package com.zhuinden.synctimer.core.networking

import android.util.Log
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Server
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.utils.register

class ConnectionManager {
    private var server: Server? = null
    private var client: Client? = null

    val isServerAvailable: Boolean get() = server != null
    val isClientAvailable: Boolean get() = client != null

    val activeServer: Server get() = server!!
    val activeClient: Client get() = client!!

    private val activeConnections: Map<Long, String> = linkedMapOf()

    fun startServer(): Boolean {
        if (server == null) {
            val server = Server()
            this.server = server
            configureKryo(server.kryo)
            server.start()
            return true
        }
        return false
    }

    fun startClient(): Boolean {
        if (client == null) {
            val client = Client()
            this.client = client
            configureKryo(client.kryo)
            client.start()
            return true
        }
        return false
    }

    fun stopServer(): Boolean {
        val server = server
        if (server != null) {
            Log.i("ConnectionManager", "Stopping server..")
            server.stop()
            Log.i("ConnectionManager", "Server stopped.")
            this.server = null
            return true
        }
        return false
    }

    fun stopClient(): Boolean {
        val client = client
        if (client != null) {
            client.stop()
            this.client = null
            return true
        }
        return false
    }

    private fun configureKryo(kryo: Kryo) {
        kryo.register<FloatArray>()
        kryo.register<IntArray>()
        kryo.register<LongArray>()
        kryo.register<Array<String>>()
        kryo.register<JoinSessionCommand>()
    }
}