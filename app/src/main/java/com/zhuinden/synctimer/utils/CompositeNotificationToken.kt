/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.utils

import com.zhuinden.eventemitter.EventSource
import java.util.*


class CompositeNotificationToken : EventSource.NotificationToken {
    private val threadId = Thread.currentThread().id

    private val notificationTokens: LinkedList<EventSource.NotificationToken> = LinkedList()

    @Suppress("MemberVisibilityCanBePrivate")
    fun add(notificationToken: EventSource.NotificationToken) {
        notificationTokens.add(notificationToken)
    }

    private var isDisposing = false

    override fun stopListening() {
        if (threadId != Thread.currentThread().id) {
            throw IllegalStateException("Cannot stop listening on a different thread where it was created")
        }
        if (isDisposing) {
            return
        }
        isDisposing = true
        val size = notificationTokens.size
        for (i in size - 1 downTo 0) {
            val token = notificationTokens.removeAt(i)
            token.stopListening()
        }
        isDisposing = false
    }

    operator fun plusAssign(notificationToken: EventSource.NotificationToken) {
        add(notificationToken)
    }
}