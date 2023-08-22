/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.start.setup

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.databinding.SetupViewBinding
import com.zhuinden.synctimer.features.start.mainmenu.MainMenuKey
import com.zhuinden.synctimer.features.start.settings.SettingsManager
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.onTextChanged

class SetupView : FrameLayout {
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

    private lateinit var binding: SetupViewBinding

    private var username: String = ""

    private val settingsManager by lazy { backstack.lookup<SettingsManager>() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = SetupViewBinding.bind(this)

        binding.inputUsername.onTextChanged { text ->
            binding.buttonSetupContinue.isEnabled = text.isNotEmpty()
            username = text
        }

        binding.buttonSetupContinue.onClick {
            settingsManager.saveUsername(username)

            backstack.setHistory(History.of(MainMenuKey()), StateChange.REPLACE)
        }
    }
}