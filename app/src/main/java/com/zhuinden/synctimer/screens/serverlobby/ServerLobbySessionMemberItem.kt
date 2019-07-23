/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.screens.serverlobby

import android.view.View
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.features.server.ServerLobbyManager
import com.zhuinden.synctimer.utils.GroupieItem
import com.zhuinden.synctimer.utils.GroupieViewHolder
import com.zhuinden.synctimer.utils.onClick
import kotlinx.android.synthetic.main.server_lobby_session_member_item.view.*

class ServerLobbySessionMemberItem(
    private val sessionMember: ServerLobbyManager.SessionMember
) : GroupieItem() {
    class ViewHolder(view: View) : GroupieViewHolder(view) {
        init {
            view.onClick { /* click */ }
        }
    }

    override fun createViewHolder(itemView: View): GroupieViewHolder = ViewHolder(itemView)

    override fun getId(): Long = sessionMember.connectionId.toLong()
    override fun equals(other: Any?): Boolean =
        other is ServerLobbySessionMemberItem && other.sessionMember == sessionMember

    override fun hashCode(): Int = sessionMember.hashCode()

    override fun getLayout(): Int = R.layout.server_lobby_session_member_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(viewHolder.root) {
            textSessionMemberUsername.text = sessionMember.username
        }
    }
}