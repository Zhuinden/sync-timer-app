package com.zhuinden.synctimer.features.synctimer

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration

import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.joinsession.JoinSessionManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyManager
import com.zhuinden.synctimer.utils.add
import com.zhuinden.synctimer.utils.lookup
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SyncTimerKey(
    val sessionType: SessionType,
    val timerConfiguration: TimerConfiguration
) : ViewKey, ScopeConfiguration.HasServices {

    override fun layout(): Int = R.layout.sync_timer_view

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val key = getKey<SyncTimerKey>()

            val serverLobbyManager =
                key.takeIf { it.sessionType == SessionType.SERVER }?.let { lookup<ServerLobbyManager>() }
            val joinSessionManager =
                key.takeIf { it.sessionType == SessionType.CLIENT }?.let { lookup<JoinSessionManager>() }

            add(
                SyncTimerManager(
                    key.sessionType,
                    key.timerConfiguration,
                    serverLobbyManager,
                    joinSessionManager,
                    backstack
                )
            )
        }
    }
}