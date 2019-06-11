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
 * when the binary form is built from using all included source files, or any of the full and complete compilation modules of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.features.joinsession

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
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.features.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.observeOnMain
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
                    connectionManager.activeClient.sendTCP(JoinSessionCommand(username))
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
            connectionManager.activeClient.sendTCP(command)
        }
    }
}