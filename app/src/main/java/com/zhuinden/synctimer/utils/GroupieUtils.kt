package com.zhuinden.synctimer.utils

import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.extensions.LayoutContainer

typealias GroupieAdapter = GroupAdapter<GroupieViewHolder>

class GroupieViewHolder(override val containerView: View) : ViewHolder(containerView), LayoutContainer

abstract class GroupieItem : Item<GroupieViewHolder>() {
    final override fun createViewHolder(itemView: View): GroupieViewHolder {
        return GroupieViewHolder(itemView)
    }
}

fun GroupAdapter<GroupieViewHolder>.replaceItemsWith(builder: MutableList<GroupieItem>.() -> Unit) {
    val list: MutableList<GroupieItem> = mutableListOf()
    list.apply(builder)
    this.update(list)
}