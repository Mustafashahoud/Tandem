package com.mustafa.tandem.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.mustafa.tandem.R
import com.mustafa.tandem.databinding.MemberItemBinding
import com.mustafa.tandem.model.Member

class MemberListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val movieOnClickCallback: ((Member) -> Unit)?
) : DataBoundListAdapter<Member, MemberItemBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.firstName == newItem.firstName
                    && oldItem.referenceCnt == newItem.referenceCnt
                    && oldItem.natives[0] == newItem.natives[0]
                    && oldItem.learns[0] == newItem.learns[0]
        }
    }
) {

    override fun createBinding(parent: ViewGroup): MemberItemBinding {
        val binding = DataBindingUtil.inflate<MemberItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.member_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.member?.let {
                movieOnClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: MemberItemBinding, item: Member) {
        binding.member = item
    }
}