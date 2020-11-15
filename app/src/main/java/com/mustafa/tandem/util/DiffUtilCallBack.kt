package com.mustafa.tandem.util

import androidx.recyclerview.widget.DiffUtil
import com.mustafa.tandem.model.Member

class DiffUtilCallBack : DiffUtil.ItemCallback<Member>() {
    override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean = oldItem == newItem
}