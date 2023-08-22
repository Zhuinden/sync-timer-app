/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.features.server.serverlobby

import android.view.View
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.databinding.ServerLobbySessionMemberItemBinding
import com.zhuinden.synctimer.utils.onClick

class ServerLobbySessionMemberItem(
    private val sessionMember: ServerLobbyManager.SessionMember
) : Item<ServerLobbySessionMemberItem.ViewHolder>() {
    class ViewHolder(view: View) : GroupieViewHolder(view) {
        val binding = ServerLobbySessionMemberItemBinding.bind(view)

        init {
            view.onClick { /* click */ }
        }
    }

    override fun createViewHolder(itemView: View): ViewHolder = ViewHolder(itemView)

    override fun getId(): Long = sessionMember.connectionId.toLong()
    override fun equals(other: Any?): Boolean =
        other is ServerLobbySessionMemberItem && other.sessionMember == sessionMember

    override fun hashCode(): Int = sessionMember.hashCode()

    override fun getLayout(): Int = R.layout.server_lobby_session_member_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        with(viewHolder.binding) {
            textSessionMemberUsername.text = sessionMember.username
        }
    }
}