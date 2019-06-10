/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.features.joinsession

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.utils.add
import com.zhuinden.synctimer.utils.lookup
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JoinSessionKey(private val placeholder: String = "") : ViewKey, ScopeConfiguration.HasServices {
    override fun layout(): Int = R.layout.join_session_view

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(JoinSessionManager(lookup(), lookup(), backstack))
        }
    }
}