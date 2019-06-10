package com.zhuinden.synctimer.features.serverlobby

import android.annotation.SuppressLint
import android.os.Parcelable
import android.util.Log
import com.esotericsoftware.kryonet.Connection
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.networking.commands.JoinSessionCommand
import com.zhuinden.synctimer.core.networking.commands.StartSessionCommand
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.utils.RxScopedService
import com.zhuinden.synctimer.utils.bindToRegistration
import com.zhuinden.synctimer.utils.observeOnMain
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.parcel.Parcelize
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import kotlin.collections.set

class ServerLobbyManager(
    private val settingsManager: SettingsManager,
    private val connectionManager: ConnectionManager,
    private val timerConfiguration: TimerConfiguration
) : RxScopedService() {
    val hostUsername: String = settingsManager.getUsername()!!

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
    fun startTimer(success: () -> Unit, failure: (Throwable) -> Unit) {
        val connectionIds = connections.values.map { registration -> registration.connectionId }

        Single.create<Unit> { emitter ->
            connectionIds.forEach { connectionId ->
                connectionManager.activeServer.sendToTCP(
                    connectionId,
                    StartSessionCommand(timerConfiguration)
                )
            }
            emitter.onSuccess(Unit)
        }.bindToRegistration(this)
            .subscribeOn(connectionManager.scheduler)
            .observeOnMain()
            .subscribeBy(onSuccess = {
                success() // TODO: navigation belongs outside of the View
            }, onError = { throwable ->
                failure(throwable)
            })
    }

    @SuppressLint("CheckResult")
    override fun onServiceRegistered() {
        super.onServiceRegistered()

        with(connectionManager) {
            startServer()

            commandReceivedEvents.observe { (connection: Connection, command: Any) ->
                when (command) {
                    is JoinSessionCommand -> {
                        addConnection(connection, command.username)

                        val connectionId = connection.id
                        connectionManager.handler.post {
                            connectionManager.activeServer.sendToTCP(connectionId, JoinSessionCommand(hostUsername))
                        }
                    }
                }
            }

            connectedEvents.observe { (connection: Connection) ->
                addConnection(connection, null)
            }

            disconnectedEvents.observe { (connection: Connection) ->
                removeConnection(connection)
            }
        }
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        connectionManager.stopServer()
    }

    private inline fun <T : Any> Observable<T>.observe(crossinline eventListener: (T) -> Unit) =
        this.bindToRegistration(this@ServerLobbyManager)
            .observeOnMain()
            .subscribeBy(onNext = { event ->
                eventListener.invoke(event)
            })

    // can I really trust this? I have no idea lol
    private var networkInterfaces: Enumeration<NetworkInterface>? = null
    private var networkAddresses: Enumeration<InetAddress>? = null

    fun getNextIp(): String {
        try {
            while (true) {
                if (this.networkInterfaces == null) {
                    networkInterfaces = NetworkInterface.getNetworkInterfaces()
                }
                if (networkAddresses == null || !networkAddresses!!.hasMoreElements()) {
                    if (networkInterfaces!!.hasMoreElements()) {
                        val networkInterface = networkInterfaces!!.nextElement()
                        networkAddresses = networkInterface.inetAddresses
                    } else {
                        networkInterfaces = null
                    }
                } else {
                    if (networkAddresses!!.hasMoreElements()) {
                        val address = networkAddresses!!.nextElement().hostAddress
                        if (address.contains(".")) {
                            return address
                        }
                    } else {
                        networkAddresses = null
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }
}