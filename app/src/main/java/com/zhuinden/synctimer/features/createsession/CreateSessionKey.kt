package com.zhuinden.synctimer.features.createsession

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateSessionKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.create_session_view
}