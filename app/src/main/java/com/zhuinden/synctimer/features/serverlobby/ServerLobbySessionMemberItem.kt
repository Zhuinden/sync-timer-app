package com.zhuinden.synctimer.features.serverlobby

import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.utils.GroupieItem
import com.zhuinden.synctimer.utils.GroupieViewHolder
import kotlinx.android.synthetic.main.server_lobby_session_member_item.view.*

class ServerLobbySessionMemberItem(
    private val sessionMember: ServerLobbyManager.SessionMember
) : GroupieItem() {
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