/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.client

import android.annotation.SuppressLint
import com.esotericsoftware.kryonet.Connection
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.core.networking.commands.StartSessionCommand
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.screens.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.observeOnMain
import com.zhuinden.synctimer.utils.tryOrNull
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class JoinSessionManager(
    private val settingsManager: SettingsManager,
    private val connectionManager: ConnectionManager,
    private val backstack: Backstack
) : RxScopedService() {
    @SuppressLint("CheckResult")
    private var hostConnectionId: Int = -1

    private val mutableIsConnected: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    val isConnected: Observable<Boolean> = mutableIsConnected

    private val mutableHostUsername: BehaviorRelay<String> = BehaviorRelay.create()
    val hostUsername: Observable<String> = mutableHostUsername

    private val mutableHostDisconnectedEvent: EventEmitter<Unit> = EventEmitter()
    val hostDisconnectedEvent: EventSource<Unit> = mutableHostDisconnectedEvent

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        val username = settingsManager.getUsername()

        connectionManager.startClient()

        connectionManager.connectedEvents
            .bindToRegistration(this)
            .observeOnMain()
            .subscribeBy { (connection) ->
                hostConnectionId = connection.id
                connectionManager.handler.post {
                    tryOrNull {
                        connectionManager.activeClient.sendTCP(JoinSessionCommand(username))
                    }
                }
                mutableIsConnected.accept(true)
            }

        connectionManager.disconnectedEvents
            .bindToRegistration(this)
            .observeOnMain()
            .subscribeBy { (connection) ->
                if (hostConnectionId == connection.id) {
                    hostConnectionId = -1
                    mutableHostUsername.accept("")
                    mutableIsConnected.accept(false)
                }
            }

        connectionManager.commandReceivedEvents
            .bindToRegistration(this)
            .observeOnMain()
            .subscribeBy { (connection: Connection, command: Any) ->
                if (connection.id == hostConnectionId) {
                    if (command is JoinSessionCommand) {
                        mutableHostUsername.accept(command.username)
                    }
                    if (command is StartSessionCommand) {
                        backstack.goTo(SyncTimerKey(SessionType.CLIENT, command.timerConfiguration))
                    }
                }
            }

        isConnected
            .distinctUntilChanged()
            .buffer(2, 1) // [a,b], [b,c], ...
            .map { connectedStates ->
                val pair: Pair<Boolean?, Boolean> = if (connectedStates.size < 2) {
                    Pair(null, connectedStates[0])
                } else {
                    Pair(connectedStates[0], connectedStates[1])
                }
                return@map pair
            }
            .bindToRegistration(this)
            .subscribeBy { (previousConnected: Boolean?, currentConnected: Boolean) ->
                if (previousConnected == true && !currentConnected) {
                    mutableHostDisconnectedEvent.emit(Unit)
                }
            }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        connectionManager.stopClient()
    }

    fun createClientSubscription(ipV4Address: String): Single<Unit> =
        connectionManager.connectClientTo(ipV4Address).observeOnMain()

    fun createBroadcastSearchSubscription(): Single<String> =
        connectionManager.searchHostViaBroadcast().map { inetAddress -> inetAddress.hostAddress }.observeOnMain()

    fun sendCommandToHost(command: Any) {
        connectionManager.handler.post {
            tryOrNull {
                connectionManager.activeClient.sendTCP(command)
            }
        }
    }
}