package com.zhuinden.synctimer.features.serverlobby

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.utils.add
import com.zhuinden.synctimer.utils.lookup
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServerLobbyKey(
    val timerConfiguration: ServerLobbyManager.TimerConfiguration
) : ViewKey, ScopeConfiguration.HasServices {
    constructor(
        startValue: Int,
        endValue: Int,
        decreaseStep: Int,
        decreaseInterval: Int
    ) : this(ServerLobbyManager.TimerConfiguration(startValue, endValue, decreaseStep, decreaseInterval))

    override fun layout(): Int = R.layout.server_lobby_view

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val key = serviceBinder.getKey<ServerLobbyKey>()
            add(ServerLobbyManager(lookup(), key.timerConfiguration))
        }
    }
}