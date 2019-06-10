/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.features.setup

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.features.mainmenu.MainMenuKey
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

            backstack.goTo(MainMenuKey())
        }
    }
}