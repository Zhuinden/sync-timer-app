/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.splash

import android.annotation.TargetApi
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.screens.mainmenu.MainMenuKey
import com.zhuinden.synctimer.screens.setup.SetupKey
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.lookup
import com.zhuinden.synctimer.utils.replaceHistory
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

    private val settingsManager by lazy { lookup<SettingsManager>() }

    private val runnable = Runnable {
        val username = settingsManager.getUsername()
        val target = when {
            username == null -> SetupKey()
            else -> MainMenuKey()
        }
        backstack.replaceHistory(target)
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