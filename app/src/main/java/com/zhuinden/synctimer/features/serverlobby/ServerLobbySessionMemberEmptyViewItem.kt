/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.serverlobby

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.utils.GroupieItem
import com.zhuinden.synctimer.utils.GroupieViewHolder

class ServerLobbySessionMemberEmptyViewItem : GroupieItem() {
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