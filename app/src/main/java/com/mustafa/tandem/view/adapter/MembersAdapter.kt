package com.mustafa.tandem.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.tandem.R
import com.mustafa.tandem.databinding.MemberItemBinding
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.util.DiffUtilCallBack

class MembersAdapter : PagingDataAdapter<Member, MembersAdapter.ViewHolder>(DiffUtilCallBack()) {
    class ViewHolder(private val binding: MemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.member = member
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MemberItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.member_item, parent, false
        )
        return ViewHolder(binding)
    }
}