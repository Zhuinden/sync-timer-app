/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.client.joinsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Patterns
import android.widget.FrameLayout
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.databinding.JoinSessionViewBinding
import com.zhuinden.synctimer.features.client.clientlobby.ClientLobbyKey
import com.zhuinden.synctimer.features.start.settings.SettingsManager
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class JoinSessionView : FrameLayout {
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

    private val joinSessionManager by lazy { backstack.lookup<JoinSessionManager>() }
    private val settingsManager by lazy { backstack.lookup<SettingsManager>() }

    private var ipV4Address: String = ""

    private val compositeDisposable = CompositeDisposable()

    private var isAttached = true

    private lateinit var binding: JoinSessionViewBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        binding = JoinSessionViewBinding.bind(this)

        binding.inputIpAddress.onTextChanged { ip ->
            this.ipV4Address = ip

            val matcher = Patterns.IP_ADDRESS.matcher(ipV4Address)

            val matches = matcher.matches()
            binding.buttonJoinSession.isEnabled = matches

            if (matches) {
                settingsManager.savePreviousHostAddress(ip)
            }
        }

        binding.inputIpAddress.setText(settingsManager.getPreviousHostAddress())

        binding.buttonBroadcastSearch.onClick {
            if (!isAttached) return@onClick

            // TODO: Rx belongs in the manager
            compositeDisposable += joinSessionManager.createBroadcastSearchSubscription()
                .subscribeBy(
                    onError = { throwable ->
                        showToast("Failed to search via broadcast") // todo
                        Log.e("JoinSession", "Failed to find host", throwable)
                    },
                    onSuccess = { host: String ->
                        binding.inputIpAddress.setText(host)
                }
            )
        }

        binding.buttonJoinSession.onClick {
            if (!isAttached) return@onClick

            // TODO: Rx belongs in the manager
            compositeDisposable += joinSessionManager.createClientSubscription(ipV4Address)
                .subscribeBy(
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false

        compositeDisposable.clear()
    }
}