/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.settings

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.lookup
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.onTextChanged
import kotlinx.android.synthetic.main.settings_view.view.*

class SettingsView : FrameLayout {
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
            username = text
            buttonSaveChanges.isEnabled = text.isNotEmpty()
        }

        username = settingsManager.getUsername()!!
        inputUsername.setText(username)

        buttonSaveChanges.onClick {
            settingsManager.saveUsername(username)
            backstack.jumpToRoot()
        }
    }
}