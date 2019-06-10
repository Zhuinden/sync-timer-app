package com.zhuinden.synctimer.features.synctimer

import android.annotation.SuppressLint
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.core.timer.TimerConfiguration
import com.zhuinden.synctimer.features.joinsession.JoinSessionManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyManager
import com.zhuinden.synctimer.utils.CompositeNotificationToken
import com.zhuinden.synctimer.utils.RxScopedService
import io.reactivex.Observable

class SyncTimerManager(
    private val sessionType: SessionType,
    private val timerConfiguration: TimerConfiguration,
    private val serverLobbyManager: ServerLobbyManager?,
    private val joinSessionManager: JoinSessionManager?,
    private val backstack: Backstack
) : RxScopedService() {
    private val mutableCurrentTime: BehaviorRelay<Int> = BehaviorRelay.createDefault(timerConfiguration.startValue)
    val currentTime: Observable<Int> = mutableCurrentTime

    private val isServer = sessionType == SessionType.SERVER
    private val isClient = sessionType == SessionType.CLIENT

    private val mutableHostDisconnectedEvent: EventEmitter<Unit> = EventEmitter()
    val hostDisconnectedEvent: EventSource<Unit> = mutableHostDisconnectedEvent

    private val compositeNotificationToken = CompositeNotificationToken()

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        if (isClient) {
            compositeNotificationToken += joinSessionManager!!.hostDisconnectedEvent.startListening {
                mutableHostDisconnectedEvent.emit(Unit)
            }
        }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()

        compositeNotificationToken.stopListening()
    }
}