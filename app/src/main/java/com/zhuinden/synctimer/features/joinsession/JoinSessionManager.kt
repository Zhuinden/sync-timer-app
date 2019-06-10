package com.zhuinden.synctimer.features.joinsession

import android.annotation.SuppressLint
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.onUI
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class JoinSessionManager(
    private val settingsManager: SettingsManager,
    private val connectionManager: ConnectionManager
) : RxScopedService() {
    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        val username = settingsManager.getUsername()

        connectionManager.startClient()

        connectionManager.connectedEvents
            .bindToRegistration(this)
            .subscribeBy { (_) ->
                // this is on looper thread
                connectionManager.activeClient.sendTCP(JoinSessionCommand(username))
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