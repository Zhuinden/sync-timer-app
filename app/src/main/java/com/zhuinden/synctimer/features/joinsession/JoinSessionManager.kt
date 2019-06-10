package com.zhuinden.synctimer.features.joinsession

import android.annotation.SuppressLint
import com.esotericsoftware.kryonet.Connection
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.onUI
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class JoinSessionManager(
    private val settingsManager: SettingsManager,
    private val connectionManager: ConnectionManager
) : RxScopedService() {
    @SuppressLint("CheckResult")
    private var hostConnectionId: Int = -1

    private val mutableHostUsername: BehaviorRelay<String> = BehaviorRelay.create()
    val hostUsername: Observable<String> = mutableHostUsername

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        val username = settingsManager.getUsername()

        connectionManager.startClient()

        connectionManager.connectedEvents
            .bindToRegistration(this)
            .onUI()
            .subscribeBy { (connection) ->
                hostConnectionId = connection.id
                connectionManager.handler.post {
                    connectionManager.activeClient.sendTCP(JoinSessionCommand(username))
                }
            }

        connectionManager.disconnectedEvents
            .bindToRegistration(this)
            .onUI()
            .subscribeBy { (connection) ->
                if (hostConnectionId == connection.id) {
                    hostConnectionId = -1
                    mutableHostUsername.accept("")
                }
            }

        connectionManager.commandReceivedEvents
            .bindToRegistration(this)
            .onUI()
            .subscribeBy { (connection: Connection, command: Any) ->
                if (connection.id == hostConnectionId) {
                    if (command is JoinSessionCommand) {
                        mutableHostUsername.accept(command.username)
                    }
                }
            }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        connectionManager.stopClient()
    }

    fun createClientSubscription(ipV4Address: String): Single<Unit> =
        connectionManager.connectClientTo(ipV4Address).onUI()

    fun createBroadcastSearchSubscription(): Single<String> =
        connectionManager.searchHostViaBroadcast().map { inetAddress -> inetAddress.hostAddress }.onUI()
}