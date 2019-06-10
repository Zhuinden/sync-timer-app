package com.zhuinden.synctimer.utils

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server

interface KryonetListener {
    fun connected(connection: Connection)
    fun disconnected(connection: Connection)
    fun received(connection: Connection, command: Any)
    fun idle(connection: Connection)
}

fun Server.addListener(kryonetListener: KryonetListener): Listener {
    val listener = object : Listener() {
        override fun connected(connection: Connection) {
            super.connected(connection)
            kryonetListener.connected(connection)
        }

        override fun disconnected(connection: Connection) {
            super.disconnected(connection)
            kryonetListener.disconnected(connection)
        }

        override fun received(connection: Connection, command: Any) {
            super.received(connection, command)
            kryonetListener.received(connection, command)
        }

        override fun idle(connection: Connection) {
            super.idle(connection)
            kryonetListener.idle(connection)
        }
    }
    this.addListener(listener)
    return listener
}

fun Client.addListener(kryonetListener: KryonetListener): Listener {
    val listener = object : Listener() {
        override fun connected(connection: Connection) {
            super.connected(connection)
            kryonetListener.connected(connection)
        }

        override fun disconnected(connection: Connection) {
            super.disconnected(connection)
            kryonetListener.disconnected(connection)
        }

        override fun received(connection: Connection, command: Any) {
            super.received(connection, command)
            kryonetListener.received(connection, command)
        }

        override fun idle(connection: Connection) {
            super.idle(connection)
            kryonetListener.idle(connection)
        }
    }
    this.addListener(listener)
    return listener
}

// kryo helpers
inline fun <reified T> Kryo.register() {
    this.register(T::class.java)
}
