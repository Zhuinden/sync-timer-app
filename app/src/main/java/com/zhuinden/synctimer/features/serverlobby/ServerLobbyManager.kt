package com.zhuinden.synctimer.features.serverlobby

import android.os.Parcelable
import android.util.Log
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.utils.KryonetListener
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.addListener
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize

class ServerLobbyManager(
    private val connectionManager: ConnectionManager,
    private val timerConfiguration: TimerConfiguration
) : RxScopedService(), KryonetListener {
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
            username?.let { registration.username = it }
        }

        members[connection.id] = SessionMember(connection.id, username ?: "[Connecting...]")
        mutableSessions.accept(members.map { it.value })
    }

    private fun removeConnection(connection: Connection) {
        connections.remove(connection.id)
        members.remove(connection.id)
        mutableSessions.accept(members.map { it.value })
    }

    private lateinit var listener: Listener

    override fun onServiceRegistered() {
        super.onServiceRegistered()
        connectionManager.startServer()

        listener = connectionManager.activeServer.addListener(this)
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        connectionManager.activeServer.removeListener(listener)

        connectionManager.stopServer() // TODO: this freezes the UI thread for 10 seconds, LOL
    }

    override fun connected(connection: Connection) {
        val registration = connections[connection.id]
        if (registration == null) {
            connections[connection.id] = ConnectionRegistration(connection, connection.id, null)
        } else {
            Log.w("ServerLobbyManager", "Unknown state: unexpected new connection with existing conn. ID")
        }
    }

    override fun disconnected(connection: Connection) {
        removeConnection(connection)
    }

    override fun received(connection: Connection, command: Any) {
        when (command) {
            is JoinSessionCommand -> {
                addConnection(connection, command.username)
            }
            else -> {
                throw IllegalArgumentException("${command.javaClass.simpleName} not handled yet")
            }
        }
    }

    override fun idle(connection: Connection) {
        // i wonder what to do here?
    }
}