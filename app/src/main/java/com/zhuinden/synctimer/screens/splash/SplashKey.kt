/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.splash

import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SplashKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.splash_view

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}