/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.client.joinsession

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import kotlinx.parcelize.Parcelize

@Parcelize
data class JoinSessionKey(private val placeholder: String = "") : ViewKey, ScopeConfiguration.HasServices {
    override fun layout(): Int = R.layout.join_session_view

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(
                JoinSessionManager(
                    settingsManager = lookup(),
                    connectionManager = lookup(),
                    backstack = backstack,
                )
            )
        }
    }
}