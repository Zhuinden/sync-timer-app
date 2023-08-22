/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.server.serverlobby

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration

import com.zhuinden.synctimer.core.timer.TimerConfiguration
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerLobbyKey(
    val timerConfiguration: TimerConfiguration
) : ViewKey, ScopeConfiguration.HasServices {
    constructor(
        startValue: Int,
        endValue: Int,
        decreaseStep: Int,
        decreaseInterval: Int
    ) : this(
        TimerConfiguration(
            startValue,
            endValue,
            decreaseStep,
            decreaseInterval,
        )
    )

    override fun layout(): Int = R.layout.server_lobby_view

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val key = serviceBinder.getKey<ServerLobbyKey>()
            add(
                ServerLobbyManager(
                    settingsManager = lookup(),
                    connectionManager = lookup(),
                    timerConfiguration = key.timerConfiguration,
                )
            )
        }
    }
}