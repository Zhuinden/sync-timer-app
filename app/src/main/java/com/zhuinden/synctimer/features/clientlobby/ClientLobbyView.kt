package com.zhuinden.synctimer.features.clientlobby

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.features.joinsession.JoinSessionManager
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.lookup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.client_lobby_view.view.*

class ClientLobbyView : FrameLayout, BackHandler {
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

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        compositeDisposable += joinSessionManager.hostUsername
            .subscribeBy { hostUsername ->
                if (hostUsername.isNotEmpty()) {
                    textClientLobbyHost.text = hostUsername
                }
            }
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