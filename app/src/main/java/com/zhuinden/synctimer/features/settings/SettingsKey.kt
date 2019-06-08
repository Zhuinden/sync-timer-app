package com.zhuinden.synctimer.features.settings

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.settings_view
}