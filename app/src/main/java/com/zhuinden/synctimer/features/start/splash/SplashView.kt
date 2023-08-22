/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.start.splash

import android.annotation.TargetApi
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.features.start.mainmenu.MainMenuKey
import com.zhuinden.synctimer.features.start.settings.SettingsManager
import com.zhuinden.synctimer.features.start.setup.SetupKey
import com.zhuinden.synctimer.utils.waitForMeasure

class SplashView : FrameLayout {
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

    private val delayProvider = Handler(Looper.getMainLooper())

    private val settingsManager by lazy { backstack.lookup<SettingsManager>() }

    private val runnable = Runnable {
        val username = settingsManager.getUsername()
        val target = when {
            username == null -> SetupKey()
            else -> MainMenuKey()
        }
        backstack.setHistory(History.of(target), StateChange.REPLACE)
    }

    private var isDetached = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        waitForMeasure { _, _, _ ->
            executeDelayed()
        }
    }

    private fun executeDelayed() {
        delayProvider.postDelayed(runnable, 1250L)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isDetached) {
            isDetached = false
            executeDelayed()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDetached = true

        handler.removeCallbacks(runnable)
    }
}