/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - The binary form of the software cannot be redistributed for commercial use under the name of a separate entity
 * when the binary form is built from using all included source files, or any of the full and complete compilation modules of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.features.joinsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Patterns
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.settings.SettingsManager
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
    private val settingsManager by lazy { lookup<SettingsManager>() }

    private var ipV4Address: String = ""

    private val compositeDisposable = CompositeDisposable()

    private var isAttached = true

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        inputIpAddress.onTextChanged { ip ->
            this.ipV4Address = ip

            val matcher = Patterns.IP_ADDRESS.matcher(ipV4Address)

            val matches = matcher.matches()
            buttonJoinSession.isEnabled = matches

            if (matches) {
                settingsManager.savePreviousHostAddress(ip)
            }
        }

        inputIpAddress.setText(settingsManager.getPreviousHostAddress())

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