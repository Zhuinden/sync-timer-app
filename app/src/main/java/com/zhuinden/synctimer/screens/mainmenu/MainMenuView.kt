/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.mainmenu

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.synctimer.databinding.MainMenuViewBinding
import com.zhuinden.synctimer.screens.createsession.CreateSessionKey
import com.zhuinden.synctimer.screens.joinsession.JoinSessionKey
import com.zhuinden.synctimer.screens.settings.SettingsKey
import com.zhuinden.synctimer.utils.onClick

class MainMenuView: FrameLayout {
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

    private lateinit var binding: MainMenuViewBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = MainMenuViewBinding.bind(this)

        binding.buttonMenuSettings.onClick {
            backstack.goTo(SettingsKey())
        }

        binding.buttonMenuCreateSession.onClick {
            backstack.goTo(CreateSessionKey())
        }

        binding.buttonMenuJoinSession.onClick {
            backstack.goTo(JoinSessionKey())
        }
    }
}