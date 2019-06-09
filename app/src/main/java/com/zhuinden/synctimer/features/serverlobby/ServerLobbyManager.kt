package com.zhuinden.synctimer.features.serverlobby

import android.annotation.SuppressLint
import android.os.Parcelable
import android.util.Log
import com.esotericsoftware.kryonet.Connection
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.parcel.Parcelize
import kotlin.collections.set

class ServerLobbyManager(
    private val connectionManager: ConnectionManager,
    private val timerConfiguration: TimerConfiguration
) : RxScopedService() {
    @Parcelize
    data class TimerConfiguration(
        val startValue: Int,
        val endValue: Int,
        val decreaseStep: Int,
        val decreaseInterval: Int
    ) : Parcelable

    private class ConnectionRegistration(
        var connection: Connection,
        var connectionId: Int,
        var username: String?
    )

    data class SessionMember(
        val connectionId: Int,
        val username: String
    )

    private val connections: LinkedHashMap<Int, ConnectionRegistration> = linkedMapOf()
    private val members: LinkedHashMap<Int, SessionMember> = linkedMapOf()

    private val mutableSessions: BehaviorRelay<List<SessionMember>> = BehaviorRelay.createDefault(emptyList())
    val sessions: Observable<List<SessionMember>> get() = mutableSessions

    private fun addConnection(connection: Connection, username: String?) {
        val registration = connections[connection.id]
        if (registration == null) {
            connections[connection.id] = ConnectionRegistration(connection, connection.id, username)
        } else {
            if (username != null) {
                registration.username = username
            } else {
                Log.w(
                    "ServerLobbyManager",
                    "Unknown state: unexpected new connection with existing conn ID [${connection.id}] :: [$username]"
                )
            }
        }

        members[connection.id] = SessionMember(connection.id, username ?: "[Connecting...]")
        mutableSessions.accept(members.map { it.value })
    }

    private fun removeConnection(connection: Connection) {
        connections.remove(connection.id)
        members.remove(connection.id)
        mutableSessions.accept(members.map { it.value })
    }

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()
        connectionManager.startServer()

        connectionManager.commandReceivedEvents.observe { (connection: Connection, command: Any) ->
            when (command) {
                is JoinSessionCommand -> {
                    addConnection(connection, command.username)
                }
            }
        }

        connectionManager.connectedEvents.observe { (connection: Connection) ->
            addConnection(connection, null)
        }

        connectionManager.disconnectedEvents.observe { (connection: Connection) ->
            removeConnection(connection)
        }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        connectionManager.stopServer()
    }

    private fun <T : Any> Observable<T>.observe(eventListener: (T) -> Unit) =
        this.bindToRegistration(this@ServerLobbyManager)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { event ->
                eventListener.invoke(event)
            })
}