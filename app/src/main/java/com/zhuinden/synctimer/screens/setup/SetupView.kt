/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.setup

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.features.settings.SettingsManager
import com.zhuinden.synctimer.screens.mainmenu.MainMenuKey
import com.zhuinden.synctimer.utils.*
import kotlinx.android.synthetic.main.setup_view.view.*

class SetupView : FrameLayout {
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

    private var username: String = ""

    private val settingsManager by lazy { lookup<SettingsManager>() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        inputUsername.onTextChanged { text ->
            buttonSetupContinue.isEnabled = text.isNotEmpty()
            username = text
        }

        buttonSetupContinue.onClick {
            settingsManager.saveUsername(username)

            backstack.replaceHistory(MainMenuKey())
        }
    }
}