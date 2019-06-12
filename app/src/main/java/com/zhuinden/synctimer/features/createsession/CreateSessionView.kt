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
 * when the binary form is built from using all included source files, or any of the compilation modules (excluding external dependencies by separate entities other than the author) of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.features.createsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyKey
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.create_session_view.view.*

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class CreateSessionView : FrameLayout {
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

    private val settingsManager by lazy { lookup<SettingsManager>() }
    private val connectionManager by lazy { lookup<ConnectionManager>() }

    private var startValue: Int = 0
    private var endValue: Int = 0
    private var decreaseStep: Int = 0
    private var decreaseInterval: Int = 0

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        startValue = settingsManager.getStartValue()
        endValue = settingsManager.getEndValue()
        decreaseStep = settingsManager.getDecreaseStep()
        decreaseInterval = settingsManager.getDecreaseInterval()

        inputStartValue.setText("$startValue")
        inputStartValue.onTextChanged { startValue ->
            if (startValue.isNotEmpty()) {
                this.startValue = startValue.toInt()
                settingsManager.saveStartValue(startValue.toInt())
            }
        }

        inputEndValue.setText("$endValue")
        inputEndValue.onTextChanged { endValue ->
            if (endValue.isNotEmpty()) {
                this.endValue = endValue.toInt()
                settingsManager.saveEndValue(endValue.toInt())
            }
        }

        inputDecreaseStep.setText("$decreaseStep")
        inputDecreaseStep.onTextChanged { decreaseStep ->
            if (decreaseStep.isNotEmpty()) {
                this.decreaseStep = decreaseStep.toInt()
                settingsManager.saveDecreaseStep(decreaseStep.toInt())
            }
        }

        inputDecreaseInterval.setText("$decreaseInterval")
        inputDecreaseInterval.onTextChanged { decreaseInterval ->
            if (decreaseInterval.isNotEmpty()) {
                this.decreaseInterval = decreaseInterval.toInt()
                settingsManager.saveDecreaseInterval(decreaseInterval.toInt())
            }
        }

        buttonCreateSession.onClick {
            backstack.goTo(ServerLobbyKey(startValue, endValue, decreaseStep, decreaseInterval))
        }
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        compositeDisposable += connectionManager.isServerBeingStopped
            .observeOnMain()
            .subscribeBy { isServerBeingStopped ->
                buttonCreateSession.isEnabled = !isServerBeingStopped
            }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isInEditMode) return

        compositeDisposable.clear()
    }
}