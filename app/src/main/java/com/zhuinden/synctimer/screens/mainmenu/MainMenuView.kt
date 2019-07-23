/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.mainmenu

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.screens.createsession.CreateSessionKey
import com.zhuinden.synctimer.screens.joinsession.JoinSessionKey
import com.zhuinden.synctimer.screens.settings.SettingsKey
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.onClick
import kotlinx.android.synthetic.main.main_menu_view.view.*

class MainMenuView: FrameLayout {
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

    override fun onFinishInflate() {
        super.onFinishInflate()

        buttonMenuSettings.onClick {
            backstack.goTo(SettingsKey())
        }

        buttonMenuCreateSession.onClick {
            backstack.goTo(CreateSessionKey())
        }

        buttonMenuJoinSession.onClick {
            backstack.goTo(JoinSessionKey())
        }
    }
}