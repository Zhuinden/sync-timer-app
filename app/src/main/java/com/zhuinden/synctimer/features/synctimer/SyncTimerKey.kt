/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - The binary form of the software cannot be redistributed for commercial use under the name of a separate entity
 * when the binary form is built from using all included source files, or any of the compilation modules (excluding external dependencies by separate entities other than the author) of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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