package com.zhuinden.synctimer.features.setup

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetupKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.setup_view
}