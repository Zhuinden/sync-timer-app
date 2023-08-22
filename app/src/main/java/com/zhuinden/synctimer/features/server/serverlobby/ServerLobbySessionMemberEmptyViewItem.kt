/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.server.serverlobby

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhuinden.synctimer.R

class ServerLobbySessionMemberEmptyViewItem : Item<GroupieViewHolder>() {
    override fun getId(): Long = -1274817L
    override fun equals(other: Any?): Boolean = other is ServerLobbySessionMemberEmptyViewItem
    override fun hashCode(): Int = -123145

    override fun getLayout(): Int = R.layout.server_lobby_session_member_empty_view

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(viewHolder.root) {
            // do nothing
        }
    }
}