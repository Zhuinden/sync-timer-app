/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.timer

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.rxcombinetuplekt.combineTuple
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.networking.commands.StartSessionCommand
import com.zhuinden.synctimer.core.networking.commands.StopTimerByUserCommand
import com.zhuinden.synctimer.core.networking.commands.TimerSyncCommand
import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.client.JoinSessionManager
import com.zhuinden.synctimer.features.server.ServerLobbyManager
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.screens.synctimer.SyncTimerView
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
    data class TimerState(
        val currentTime: Int,
        val stoppingPlayer: String?,
        val isTimerStoppedByPlayer: Boolean,
        val isTimerStarted: Boolean,
        val isTimerPaused: Boolean,
        val isTimerReachedEnd: Boolean
    )

    private val mutableCurrentTime: BehaviorRelay<Int> = BehaviorRelay.createDefault(timerConfiguration.startValue)
    private val mutableStoppingPlayer: BehaviorRelay<String> = BehaviorRelay.createDefault("")
    private val mutableIsTimerStarted: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    private val mutableIsTimerPaused: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    private val mutableIsTimerReachedEnd: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)

    val timerState: Observable<TimerState> = combineTuple(
        mutableCurrentTime,
        mutableStoppingPlayer,
        mutableIsTimerStarted,
        mutableIsTimerPaused,
        mutableIsTimerReachedEnd
    ).map { (currentTime: Int, stoppingPlayer: String, isTimerStarted: Boolean, isTimerPaused: Boolean, isTimerReachedEnd: Boolean) ->
        TimerState(
            currentTime,
            stoppingPlayer,
            stoppingPlayer.takeIf { it.isNotEmpty() } != null,
            isTimerStarted,
            isTimerPaused,
            isTimerReachedEnd,
        )
    }

    class ConfirmationEvent(val onPositiveClick: () -> Unit)

    private val mutableConfirmationEvents = EventEmitter<ConfirmationEvent>()
    val confirmationEvents: EventSource<ConfirmationEvent> = mutableConfirmationEvents

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
            connectionManager.connectedEvents
                .bindToRegistration(this)
                .subscribeBy { (connection) ->
                    // this is on looper thread
                    connectionManager.activeServer.sendToTCP(connection.id, StartSessionCommand(timerConfiguration))

                    handler.post {
                        serverSendSyncCommand()
                    }
                }

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

            val stoppingPlayer = mutableStoppingPlayer.value!!

            if (stoppingPlayer.isEmpty()) {
                serverStartTimer()
            } else {
                mutableConfirmationEvents.emit(ConfirmationEvent {
                    serverStartTimer()
                })
            }
        }
    }

    private fun serverStartTimer() {
        serverIsCountdownActive = true
        mutableStoppingPlayer.accept("")
        mutableIsTimerPaused.accept(false)

        serverSendSyncCommand()
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
                mutableStoppingPlayer.value?.takeIf { it.isNotEmpty() } != null,
                mutableStoppingPlayer.value!!,
                mutableIsTimerPaused.value!!,
                mutableIsTimerReachedEnd.value!!
            )
        )
    }
}