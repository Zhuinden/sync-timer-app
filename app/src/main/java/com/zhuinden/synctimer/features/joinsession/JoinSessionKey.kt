package com.zhuinden.synctimer.features.joinsession

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JoinSessionKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.join_session_view
}