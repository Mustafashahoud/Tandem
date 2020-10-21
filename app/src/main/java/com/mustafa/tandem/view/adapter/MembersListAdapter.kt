package com.mustafa.tandem.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.mustafa.tandem.R
import com.mustafa.tandem.databinding.MemberItemBinding
import com.mustafa.tandem.model.Member

class MembersListAdapter(
    private val dataBindingComponent: DataBindingComponent,
) : BaseAdapter<Member, MemberItemBinding>(

) {
    override fun createBinding(parent: ViewGroup): MemberItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.member_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: MemberItemBinding, item: Member) {
        binding.member = item
    }

}