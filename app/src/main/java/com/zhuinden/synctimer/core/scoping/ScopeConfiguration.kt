/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.core.scoping

import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder

class ScopeConfiguration : ScopedServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        val key = serviceBinder.getKey<Any>()
        if (key is HasServices) {
            key.bindServices(serviceBinder)
        }
    }

    interface HasServices : ScopeKey {
        fun bindServices(serviceBinder: ServiceBinder)

        override fun getScopeTag(): String = javaClass.name
    }
}