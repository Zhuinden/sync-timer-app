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
package com.zhuinden.synctimer.features.synctimer

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.StateChange
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.sync_timer_view.view.*

class SyncTimerView : FrameLayout, BackHandler {
    interface ActionHandler {
        fun onStartTimerClicked()
        fun onResetTimerClicked()
        fun onStopTimerClicked()
        fun onPauseTimerClicked()
        fun onUnpauseTimerClicked()
    }

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

    private val syncTimerManager by lazy { lookup<SyncTimerManager>() }
    private val actionHandler by lazy { lookup<ActionHandler>() }

    private fun showLeavingAlert() {
        AlertDialog.Builder(context)
            .setTitle("Leaving session")
            .setMessage("Are you sure you want to quit the session?")
            .setPositiveButton("Quit") { _, _ ->
                backstack.jumpToRoot(StateChange.REPLACE)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        buttonExitSession.onClick {
            showLeavingAlert()
        }

        buttonStartTimer.onClick {
            actionHandler.onStartTimerClicked()
        }

        buttonStopTimer.onClick {
            actionHandler.onStopTimerClicked()
        }

        buttonResetTimer.onClick {
            actionHandler.onResetTimerClicked()
        }

        buttonPauseTimer.onClick {
            actionHandler.onPauseTimerClicked()
        }

        buttonUnpauseTimer.onClick {
            actionHandler.onUnpauseTimerClicked()
        }
    }

    private val compositeDisposable = CompositeDisposable()
    private val compositeNotificationToken = CompositeNotificationToken()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        val key = getKey<SyncTimerKey>()

        with(syncTimerManager) {
            compositeDisposable += this.currentTime.combineWith(
                stoppingPlayer,
                isTimerStarted,
                isTimerPaused,
                isTimerReachedEnd
            )
                .subscribeBy { (currentTime, stoppingPlayer, isTimerStarted, isTimerPaused, isTimerReachedEnd) ->
                    textCountdownTime.text = "${currentTime}"

                    textStoppedIndicator.text = when {
                        stoppingPlayer.isNotEmpty() -> "Stopped!"
                        isTimerPaused -> "PAUSED!"
                        isTimerReachedEnd -> "The timer has reached the end."
                        isTimerStarted -> "The countdown is on!"
                        else -> "Waiting for start."
                    }
                    textStopperNameText.text = when {
                        stoppingPlayer.isNotEmpty() -> "$stoppingPlayer has stopped the timer!"
                        else -> ""
                    }

                    buttonStopTimer.showIf { isTimerStarted && !isTimerPaused && !isTimerReachedEnd }
                    buttonStartTimer.showIf { key.isHost() && !isTimerStarted && !isTimerReachedEnd && !isTimerPaused }
                    buttonResetTimer.showIf { key.isHost() && !isTimerStarted && (stoppingPlayer.isNotEmpty() || isTimerReachedEnd) }
                    buttonPauseTimer.showIf { key.isHost() && isTimerStarted && !isTimerPaused }
                    buttonUnpauseTimer.showIf { key.isHost() && isTimerPaused }
                }
        }

        compositeNotificationToken += syncTimerManager.hostDisconnectedEvent  // TODO: DUPLICATION #1874 (kind of)
            .startListening { _ ->
                showLongToast(R.string.alert_host_disconnected)
                backstack.jumpToRoot(StateChange.REPLACE) // TODO: this belongs in managers, but right now it'd be duplicate events
            }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isInEditMode) return

        compositeDisposable.clear()
        compositeNotificationToken.stopListening()
    }

    override fun onBackPressed(): Boolean {
        showLeavingAlert()
        return true
    }
}