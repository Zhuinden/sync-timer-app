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
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.core.networking.SessionType
import com.zhuinden.synctimer.databinding.ServerLobbyViewBinding
import com.zhuinden.synctimer.features.server.ServerLobbyManager
import com.zhuinden.synctimer.screens.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.showToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class ServerLobbyView : FrameLayout, BackHandler {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private lateinit var binding: ServerLobbyViewBinding

    private val serverLobbyManager by lazy { backstack.lookup<ServerLobbyManager>() }

    private val adapter = GroupieAdapter()

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        binding = ServerLobbyViewBinding.bind(this)

        binding.recyclerSessionMembers.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        binding.buttonStartTimer.onClick {
            // TODO: this all belongs outside of the view
            serverLobbyManager.startTimerForAllPlayers(failure = { err ->
                showToast("Could not start all timers. Aborting...")
                backstack.jumpToRoot()
            }, success = {
                backstack.goTo(
                    SyncTimerKey(
                        SessionType.SERVER,
                        serverLobbyManager.timerConfiguration
                    )
                )
            })
        }

        binding.textHostIpAddress.text = serverLobbyManager.getNextIp()

        binding.buttonNextIpAddress.onClick {
            binding.textHostIpAddress.text = serverLobbyManager.getNextIp()
        }
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        compositeDisposable += serverLobbyManager.sessions.subscribeBy { members ->
            adapter.replaceAll(mutableListOf<Group>().apply {
                if (members.isEmpty()) {
                    add(ServerLobbySessionMemberEmptyViewItem())
                } else {
                    addAll(members.map { ServerLobbySessionMemberItem(it) })
                }
            })
            binding.recyclerSessionMembers.adapter = adapter

            binding.buttonStartTimer.isEnabled =
                true // enable ability to start timer, we support connecting later
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