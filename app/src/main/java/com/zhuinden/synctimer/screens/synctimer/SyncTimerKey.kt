/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.synctimer

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensions.servicesktx.rebind
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.client.JoinSessionManager
import com.zhuinden.synctimer.features.server.ServerLobbyManager
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.features.timer.SyncTimerManager
import kotlinx.parcelize.Parcelize

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