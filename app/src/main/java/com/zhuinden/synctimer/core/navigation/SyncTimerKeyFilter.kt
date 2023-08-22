/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.core.navigation

import com.zhuinden.simplestack.KeyFilter
import com.zhuinden.synctimer.features.client.clientlobby.ClientLobbyKey
import com.zhuinden.synctimer.features.server.serverlobby.ServerLobbyKey
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