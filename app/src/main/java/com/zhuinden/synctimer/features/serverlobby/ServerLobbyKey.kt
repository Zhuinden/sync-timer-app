package com.zhuinden.synctimer.features.serverlobby

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServerLobbyKey(private val placeholder: String = "") : ViewKey {
    override fun layout(): Int = R.layout.server_lobby_view
}