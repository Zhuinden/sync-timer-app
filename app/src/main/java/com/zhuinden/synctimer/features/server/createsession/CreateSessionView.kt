/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.server.createsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.databinding.CreateSessionViewBinding
import com.zhuinden.synctimer.features.server.serverlobby.ServerLobbyKey
import com.zhuinden.synctimer.features.start.settings.SettingsManager
import com.zhuinden.synctimer.utils.observeOnMain
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class CreateSessionView : FrameLayout {
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

    private val settingsManager by lazy { backstack.lookup<SettingsManager>() }
    private val connectionManager by lazy { backstack.lookup<ConnectionManager>() }

    private var startValue: Int = 0
    private var endValue: Int = 0
    private var decreaseStep: Int = 0
    private var decreaseInterval: Int = 0

    private lateinit var binding: CreateSessionViewBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        binding = CreateSessionViewBinding.bind(this)

        startValue = settingsManager.getStartValue()
        endValue = settingsManager.getEndValue()
        decreaseStep = settingsManager.getDecreaseStep()
        decreaseInterval = settingsManager.getDecreaseInterval()

        binding.inputStartValue.setText("$startValue")
        binding.inputStartValue.onTextChanged { startValue ->
            if (startValue.isNotEmpty()) {
                this.startValue = startValue.toInt()
                settingsManager.saveStartValue(startValue.toInt())
            }
        }

        binding.inputEndValue.setText("$endValue")
        binding.inputEndValue.onTextChanged { endValue ->
            if (endValue.isNotEmpty()) {
                this.endValue = endValue.toInt()
                settingsManager.saveEndValue(endValue.toInt())
            }
        }

        binding.inputDecreaseStep.setText("$decreaseStep")
        binding.inputDecreaseStep.onTextChanged { decreaseStep ->
            if (decreaseStep.isNotEmpty()) {
                this.decreaseStep = decreaseStep.toInt()
                settingsManager.saveDecreaseStep(decreaseStep.toInt())
            }
        }

        binding.inputDecreaseInterval.setText("$decreaseInterval")
        binding.inputDecreaseInterval.onTextChanged { decreaseInterval ->
            if (decreaseInterval.isNotEmpty()) {
                this.decreaseInterval = decreaseInterval.toInt()
                settingsManager.saveDecreaseInterval(decreaseInterval.toInt())
            }
        }

        binding.buttonCreateSession.onClick {
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
                binding.buttonCreateSession.isEnabled = !isServerBeingStopped
            }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isInEditMode) return

        compositeDisposable.clear()
    }
}