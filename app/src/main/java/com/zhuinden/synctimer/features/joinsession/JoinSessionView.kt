package com.zhuinden.synctimer.features.joinsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Patterns
import android.widget.FrameLayout
import com.zhuinden.simplestack.StateChange
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.features.clientlobby.ClientLobbyKey
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.join_session_view.view.*

class JoinSessionView : FrameLayout {
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

    private val joinSessionManager by lazy { lookup<JoinSessionManager>() }

    private var ipV4Address: String = ""

    private val compositeDisposable = CompositeDisposable()

    private var isAttached = true

    override fun onFinishInflate() {
        super.onFinishInflate()

        inputIpAddress.onTextChanged { ip ->
            this.ipV4Address = ip

            val matcher = Patterns.IP_ADDRESS.matcher(ipV4Address)
            buttonJoinSession.isEnabled = matcher.matches()
        }

        buttonBroadcastSearch.onClick {
            if (!isAttached) return@onClick

            // TODO: Rx belongs in the manager
            compositeDisposable += joinSessionManager.createBroadcastSearchSubscription().subscribeBy(
                onError = { throwable ->
                    showToast("Failed to search via broadcast") // todo
                    Log.e("JoinSession", "Failed to find host", throwable)
                },
                onSuccess = { host: String ->
                    inputIpAddress.setText(host)
                }
            )
        }

        buttonJoinSession.onClick {
            if (!isAttached) return@onClick

            // TODO: Rx belongs in the manager
            compositeDisposable += joinSessionManager.createClientSubscription(ipV4Address).subscribeBy(
                onError = { throwable ->
                    showToast("Failed to connect to $ipV4Address") // todo
                    Log.e("JoinSession", "Failed to connect", throwable)
                },
                onSuccess = {
                    backstack.goTo(ClientLobbyKey())
                }
            )
        }
    }

    private val compositeNotificationToken = CompositeNotificationToken()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true

        compositeNotificationToken += joinSessionManager.hostDisconnectedEvent.startListening {
            showLongToast(R.string.alert_host_disconnected)
            backstack.jumpToRoot(StateChange.REPLACE) // TODO: this belongs in managers, but right now it'd be duplicate events
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false

        compositeNotificationToken.stopListening()
        compositeDisposable.clear()
    }
}