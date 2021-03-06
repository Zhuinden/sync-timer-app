/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.createsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.screens.serverlobby.ServerLobbyKey
import com.zhuinden.synctimer.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.create_session_view.view.*

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