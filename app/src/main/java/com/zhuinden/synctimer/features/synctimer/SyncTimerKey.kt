package com.zhuinden.synctimer.features.synctimer

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SyncTimerKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.sync_timer_view
}