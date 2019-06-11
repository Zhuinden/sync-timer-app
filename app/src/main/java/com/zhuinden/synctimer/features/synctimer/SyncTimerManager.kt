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
package com.zhuinden.synctimer.features.synctimer

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.networking.commands.StopTimerByUserCommand
import com.zhuinden.synctimer.core.networking.commands.TimerSyncCommand
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.joinsession.JoinSessionManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyManager
import com.zhuinden.synctimer.utils.CompositeNotificationToken
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.observeOnMain
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class SyncTimerManager(
    private val sessionType: SessionType,
    private val timerConfiguration: TimerConfiguration,
    private val connectionManager: ConnectionManager,
    private val serverLobbyManager: ServerLobbyManager?,
    private val joinSessionManager: JoinSessionManager?,
    private val settingsManager: SettingsManager
) : RxScopedService(), SyncTimerView.ActionHandler {
    private val mutableCurrentTime: BehaviorRelay<Int> = BehaviorRelay.createDefault(timerConfiguration.startValue)
    val currentTime: Observable<Int> = mutableCurrentTime

    private val mutableStoppingPlayer: BehaviorRelay<String> = BehaviorRelay.createDefault("")
    val stoppingPlayer: Observable<String> = mutableStoppingPlayer

    fun isStoppedByPlayer() = (mutableStoppingPlayer.value ?: "").isNotEmpty()

    private val mutableIsTimerStarted: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    val isTimerStarted: Observable<Boolean> = mutableIsTimerStarted

    private val mutableIsTimerPaused: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    val isTimerPaused: Observable<Boolean> = mutableIsTimerPaused

    private val mutableIsTimerReachedEnd: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    val isTimerReachedEnd: Observable<Boolean> = mutableIsTimerReachedEnd

    private var serverIsCountdownActive = false
        set(value) {
            field = value
            mutableIsTimerStarted.accept(value)
            handler.removeCallbacks(serverDecrementAction)
            if (value) {
                serverScheduleDecrement()
            }
        }

    private val handler = Handler(Looper.getMainLooper())
    private val serverDecrementAction = Runnable {
        val currentTimeValue = mutableCurrentTime.value!!
        val newTimeValue = currentTimeValue - timerConfiguration.decreaseStep

        if (newTimeValue >= timerConfiguration.endValue) {
            mutableCurrentTime.accept(newTimeValue)
        } else {
            mutableIsTimerReachedEnd.accept(true)
            serverIsCountdownActive = false
        }
        serverSendSyncCommand()
        if (serverIsCountdownActive) {
            serverScheduleDecrement()
        }
    }

    private fun serverScheduleDecrement() {
        handler.postDelayed(serverDecrementAction, timerConfiguration.decreaseInterval * 1000L)
    }

    private val isServer = sessionType == SessionType.SERVER
    private val isClient = sessionType == SessionType.CLIENT

    private val mutableHostDisconnectedEvent: EventEmitter<Unit> = EventEmitter()
    val hostDisconnectedEvent: EventSource<Unit> = mutableHostDisconnectedEvent

    private val compositeNotificationToken = CompositeNotificationToken()

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!

            compositeNotificationToken += joinSessionManager.hostDisconnectedEvent.startListening {
                mutableHostDisconnectedEvent.emit(Unit)
            }

            connectionManager.commandReceivedEvents
                .bindToRegistration(this)
                .observeOnMain()
                .subscribeBy { (_, command) ->
                    if (command is TimerSyncCommand) {
                        mutableCurrentTime.accept(command.currentTime)
                        mutableStoppingPlayer.accept(command.stoppedBy)
                        mutableIsTimerPaused.accept(command.isPaused)
                        mutableIsTimerStarted.accept(command.isCountdownActive)
                        mutableIsTimerReachedEnd.accept(command.isTimerReachedEnd)
                    }
                }
        }

        if (isServer) {
            connectionManager.commandReceivedEvents
                .bindToRegistration(this)
                .observeOnMain()
                .subscribeBy { (_, command) ->
                    if (command is StopTimerByUserCommand) {
                        handleStopByPlayer(command.username)
                    }
                }
        }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()

        compositeNotificationToken.stopListening()
    }

    override fun onStartTimerClicked() {
        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!
            // we should not get here, only the host can start the timer.
        }

        if (isServer) { // TODO: this is a smell, clearly there are two things in one here.
            val serverLobbyManager = serverLobbyManager!!

            serverIsCountdownActive = true
            mutableStoppingPlayer.accept("")
            mutableIsTimerPaused.accept(false)

            serverSendSyncCommand()
        }
    }

    override fun onResetTimerClicked() {
        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!
            // we should not get here, only the host can reset the timer.
        }

        if (isServer) { // TODO: this is a smell, clearly there are two things in one here.
            val serverLobbyManager = serverLobbyManager!!

            serverIsCountdownActive = false

            mutableCurrentTime.accept(timerConfiguration.startValue)
            mutableStoppingPlayer.accept("")
            mutableIsTimerPaused.accept(false)
            mutableIsTimerReachedEnd.accept(false)

            serverSendSyncCommand()
        }
    }

    override fun onStopTimerClicked() {
        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!

            joinSessionManager.sendCommandToHost(StopTimerByUserCommand(settingsManager.getUsername()!!))
            // don't forget to handle this in `received()`
        }

        if (isServer) { // TODO: this is a smell, clearly there are two things in one here.
            handleStopByPlayer(settingsManager.getUsername()!!)
        }
    }

    private fun handleStopByPlayer(username: String) {
        serverIsCountdownActive = false

        mutableStoppingPlayer.accept(username)
        mutableIsTimerPaused.accept(false)

        serverSendSyncCommand()
    }

    override fun onPauseTimerClicked() {
        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!

            // we should not get here, only the host can pause the timer.
            // the clients can only *stop* it, tbh.
        }

        if (isServer) { // TODO: this is a smell, clearly there are two things in one here.
            val serverLobbyManager = serverLobbyManager!!

            serverIsCountdownActive = false

            mutableIsTimerPaused.accept(true)

            serverSendSyncCommand()
        }
    }

    override fun onUnpauseTimerClicked() {
        if (isClient) { // TODO: this is a smell, clearly there are two things in one here.
            val joinSessionManager = joinSessionManager!!

            // we should not get here, only the host can unpause the timer.
            // the clients can only *stop* it, tbh.
        }

        if (isServer) { // TODO: this is a smell, clearly there are two things in one here.
            val serverLobbyManager = serverLobbyManager!!

            serverIsCountdownActive = true

            mutableIsTimerPaused.accept(false)
            mutableStoppingPlayer.accept("")

            serverSendSyncCommand()
        }
    }

    private fun serverSendSyncCommand() {
        serverLobbyManager!!.sendCommandToAll(
            TimerSyncCommand(
                mutableCurrentTime.value!!,
                serverIsCountdownActive,
                isStoppedByPlayer(),
                mutableStoppingPlayer.value!!,
                mutableIsTimerPaused.value!!,
                mutableIsTimerReachedEnd.value!!
            )
        )
    }
}