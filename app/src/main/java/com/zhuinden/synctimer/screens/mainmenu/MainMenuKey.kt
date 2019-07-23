/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.mainmenu

import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainMenuKey(private val placeholder: String = ""): ViewKey {
    override fun viewChangeHandler(): ViewChangeHandler = FadeViewChangeHandler()

    override fun layout(): Int = R.layout.main_menu_view
}