/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.core.networking

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.zhuinden.synctimer.utils.KryonetListener
import com.zhuinden.synctimer.utils.addListener
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicReference

class ConnectionManager {
    private val kryonetListener = object : KryonetListener {
        override fun connected(connection: Connection) {
            mutableConnectedEvents.accept(ConnectedEvent(connection))
        }

        override fun disconnected(connection: Connection) {
            mutableDisconnectedEvents.accept(DisconnectedEvent(connection))
        }

        override fun received(connection: Connection, command: Any) {
            mutableCommandReceivedEvents.accept(CommandReceivedEvent(connection, command))
        }

        override fun idle(connection: Connection) {
            mutableIdleEvents.accept(IdleEvent(connection))
        }
    }

    companion object {
        const val TAG = "ConnectionManager"

        const val TCP_PORT = 4000
        const val UDP_PORT = 5555
    }

    data class ConnectedEvent(val connection: Connection)
    data class DisconnectedEvent(val connection: Connection)
    data class CommandReceivedEvent(val connection: Connection, val command: Any)
    data class IdleEvent(val connection: Connection)

    private val mutableConnectedEvents: PublishRelay<ConnectedEvent> = PublishRelay.create()
    private val mutableDisconnectedEvents: PublishRelay<DisconnectedEvent> = PublishRelay.create()
    private val mutableCommandReceivedEvents: PublishRelay<CommandReceivedEvent> = PublishRelay.create()
    private val mutableIdleEvents: PublishRelay<IdleEvent> = PublishRelay.create()

    val connectedEvents: Observable<ConnectedEvent> = mutableConnectedEvents
    val disconnectedEvents: Observable<DisconnectedEvent> = mutableDisconnectedEvents
    val commandReceivedEvents: Observable<CommandReceivedEvent> = mutableCommandReceivedEvents
    val idleEvents: Observable<IdleEvent> = mutableIdleEvents

    private val mutableIsServerBeingStopped: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    val isServerBeingStopped: Observable<Boolean> = mutableIsServerBeingStopped

    private val looperThread: HandlerThread = HandlerThread("CONNECTION-MANAGER[${hashCode()}]")
    val handler: Handler
    val scheduler: Scheduler

    init {
        looperThread.start()

        synchronized(looperThread) {
            val looper = looperThread.looper
            handler = Handler(looper)
            scheduler = AndroidSchedulers.from(looper)
        }
    }

    private val server: AtomicReference<Server?> = AtomicReference()
    private val client: AtomicReference<Client?> = AtomicReference()

    val activeServer: Server get() = server.get()!!
    val activeClient: Client get() = client.get()!!

    private val serverStart = Runnable {
        synchronized(this) {
            val currentServer = server.get()
            if (currentServer == null) {
                val server = Server()
                this.server.set(server)
                KryoRegistrar.configureKryo(server.kryo)
                Log.i(TAG, "Starting server")
                server.start()
                Log.i(TAG, "Server started")
                server.bind(TCP_PORT, UDP_PORT)
                server.addListener(kryonetListener)
            }
        }
    }

    fun startServer() {
        handler.post(serverStart)
    }

    private val clientStart = Runnable {
        synchronized(this) {
            val currentClient = client.get()
            if (currentClient == null) {
                val client = Client()
                this.client.set(client)
                KryoRegistrar.configureKryo(client.kryo)
                Log.i(TAG, "Starting client")
                client.start()
                Log.i(TAG, "Client started")
                client.addListener(kryonetListener)
            }
        }
    }

    fun startClient() {
        handler.post(clientStart)
    }

    private val serverStop = Runnable {
        synchronized(this) {
            val server = server.getAndSet(null)
            if (server != null) {
                Log.i(TAG, "Stopping server")
                mutableIsServerBeingStopped.accept(true)
                server.stop()
                mutableIsServerBeingStopped.accept(false)
                Log.i(TAG, "Server stopped")
            }
        }
    }

    fun stopServer() {
        handler.post(serverStop)
    }

    private val clientStop = Runnable {
        synchronized(this) {
            val client = client.getAndSet(null)
            if (client != null) {
                Log.i(TAG, "Stopping client")
                client.stop()
                Log.i(TAG, "Client stopped")
            }
        }
    }

    fun stopClient() {
        handler.post(clientStop)
    }

    fun connectClientTo(ipV4Address: String): Single<Unit> = Single.create { emitter ->
        handler.post {
            synchronized(this) {
                val client = client.get()
                if (client == null) {
                    emitter.onError(IllegalStateException("Client was not started"))
                    return@synchronized
                }

                try {
                    client.connect(5000, ipV4Address, 6000, 6555) // 6000, 6555 is for emulators only
                    emitter.onSuccess(Unit)
                } catch (e: Throwable) {
                    emitter.onError(e)
                    return@synchronized
                }
            }
        }
    }

    fun searchHostViaBroadcast(): Single<InetAddress> = Single.create { emitter ->
        handler.post {
            synchronized(this) {
                val client = client.get()
                if (client == null) {
                    emitter.onError(IllegalStateException("Client was not started"))
                    return@synchronized
                }

                try {
                    val host = client.discoverHost(UDP_PORT, 5000)
                    if (host == null) {
                        emitter.onError(IllegalStateException("Could not find host"))
                    } else {
                        emitter.onSuccess(host)
                    }
                } catch (e: Throwable) {
                    emitter.onError(e)
                    return@synchronized
                }
            }
        }
    }
}