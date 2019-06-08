package com.zhuinden.synctimer.core.scoping

import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder

class ScopeConfiguration : ScopedServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        val key = serviceBinder.getKey<Any>()
        if (key is HasServices) {
            key.bindServices(serviceBinder)
        }
    }

    interface HasServices {
        fun bindServices(serviceBinder: ServiceBinder)
    }
}