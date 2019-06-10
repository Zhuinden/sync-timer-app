/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.core.navigation

import com.zhuinden.simplestack.KeyFilter
import com.zhuinden.synctimer.features.clientlobby.ClientLobbyKey
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyKey
import com.zhuinden.synctimer.features.synctimer.SyncTimerKey

class SyncTimerKeyFilter : KeyFilter {
    override fun filterHistory(restoredKeys: MutableList<Any>): MutableList<Any> {
        val iterator = restoredKeys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if ((key is ServerLobbyKey) or (key is ClientLobbyKey) or (key is SyncTimerKey)) {
                iterator.remove() // TODO: session restoration is not supported
            }
        }
        return restoredKeys
    }
}