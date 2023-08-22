/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.start.settings

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.settings_view
}