/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.setup

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetupKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.setup_view
}