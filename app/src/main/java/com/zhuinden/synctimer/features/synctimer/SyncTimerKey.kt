/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.features.synctimer

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.core.settings.SettingsManager

import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.joinsession.JoinSessionManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyManager
import com.zhuinden.synctimer.utils.add
import com.zhuinden.synctimer.utils.get
import com.zhuinden.synctimer.utils.lookup
import com.zhuinden.synctimer.utils.rebind
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SyncTimerKey(
    val sessionType: SessionType,
    val timerConfiguration: TimerConfiguration
) : ViewKey, ScopeConfiguration.HasServices {
    fun isHost() = sessionType == SessionType.SERVER
    fun isClient() = sessionType == SessionType.CLIENT

    override fun layout(): Int = R.layout.sync_timer_view

    @Suppress("RemoveExplicitTypeArguments")
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
                    lookup<ConnectionManager>(),
                    serverLobbyManager,
                    joinSessionManager,
                    lookup<SettingsManager>()
                )
            )

            rebind<SyncTimerView.ActionHandler>(get<SyncTimerManager>())
        }
    }
}