package com.zhuinden.synctimer.features.joinsession

import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.onUI
import io.reactivex.Single

class JoinSessionManager(
    private val connectionManager: ConnectionManager
) : RxScopedService() {
    override fun onServiceRegistered() {
        super.onServiceRegistered()
        connectionManager.startClient()
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