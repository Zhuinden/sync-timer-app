/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.serverlobby

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.features.server.ServerLobbyManager
import com.zhuinden.synctimer.screens.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.server_lobby_view.view.*

class ServerLobbyView : FrameLayout, BackHandler {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private val serverLobbyManager by lazy { lookup<ServerLobbyManager>() }

    private val adapter = GroupieAdapter()

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        recyclerSessionMembers.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        buttonStartTimer.onClick {
            // TODO: this all belongs outside of the view
            serverLobbyManager.startTimerForAllPlayers(failure = { err ->
                showToast("Could not start all timers. Aborting...")
                backstack.jumpToRoot()
            }, success = {
                backstack.goTo(SyncTimerKey(SessionType.SERVER, serverLobbyManager.timerConfiguration))
            })
        }

        textHostIpAddress.text = serverLobbyManager.getNextIp()

        buttonNextIpAddress.onClick {
            textHostIpAddress.text = serverLobbyManager.getNextIp()
        }
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        compositeDisposable += serverLobbyManager.sessions.subscribeBy { members ->
            adapter.replaceItemsWith {
                if (members.isEmpty()) {
                    add(ServerLobbySessionMemberEmptyViewItem())
                } else {
                    addAll(members.map { ServerLobbySessionMemberItem(it) })
                }
            }
            recyclerSessionMembers.adapter = adapter

            buttonStartTimer.isEnabled = true // enable ability to start timer, we support connecting later
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isInEditMode) return

        compositeDisposable.clear()
    }


    override fun onBackPressed(): Boolean {
        AlertDialog.Builder(context)
            .setTitle("Leaving session")
            .setMessage("Are you sure you want to quit the session?")
            .setPositiveButton("Quit") { _, _ ->
                backstack.goBack()
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
        return true
    }
}