package com.zhuinden.synctimer.features.serverlobby

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhuinden.synctimer.features.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.server_lobby_view.view.*

class ServerLobbyView : FrameLayout {
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

        buttonBlahGoToTimer.onClick {
            backstack.goTo(SyncTimerKey())
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
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isInEditMode) return

        compositeDisposable.clear()
    }
}