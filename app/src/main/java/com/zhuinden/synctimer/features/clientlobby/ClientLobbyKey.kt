package com.zhuinden.synctimer.features.clientlobby

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClientLobbyKey(private val placeholder: String = "") : ViewKey {
    override fun layout(): Int = R.layout.client_lobby_view
}