/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.settings

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.databinding.SettingsViewBinding
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.onTextChanged

class SettingsView : FrameLayout {
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

    private lateinit var binding: SettingsViewBinding

    private var username: String = ""

    private val settingsManager by lazy { backstack.lookup<SettingsManager>() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = SettingsViewBinding.bind(this)

        binding.inputUsername.onTextChanged { text ->
            username = text
            binding.buttonSaveChanges.isEnabled = text.isNotEmpty()
        }

        username = settingsManager.getUsername()!!
        binding.inputUsername.setText(username)

        binding.buttonSaveChanges.onClick {
            settingsManager.saveUsername(username)
            backstack.jumpToRoot()
        }
    }
}