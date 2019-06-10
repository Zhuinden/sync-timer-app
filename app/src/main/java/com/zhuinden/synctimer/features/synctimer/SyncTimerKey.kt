package com.zhuinden.synctimer.features.synctimer

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SyncTimerKey(val sessionType: SessionType) : ViewKey {
    enum class SessionType {
        SERVER,
        CLIENT
    }
    override fun layout(): Int = R.layout.sync_timer_view
}