package com.mustafa.tandem.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.tandem.R
import com.mustafa.tandem.binding.FragmentDataBindingComponent
import com.mustafa.tandem.databinding.CommunityFragmentBinding
import com.mustafa.tandem.di.Injectable
import com.mustafa.tandem.model.Status
import com.mustafa.tandem.util.RetryCallback
import com.mustafa.tandem.util.autoCleared
import com.mustafa.tandem.view.adapter.MemberListAdapter
import javax.inject.Inject

class CommunityFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<CommunityFragmentBinding>()

    private var adapter by autoCleared<MemberListAdapter>()

    private val viewModel by viewModels<CommunityViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.community_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            lifecycleOwner = this@CommunityFragment
            vm = viewModel
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initRecyclerView()
        subscribeUi()

    }

    private fun initRecyclerView() {
        adapter = MemberListAdapter(dataBindingComponent = dataBindingComponent) {}
        binding.membersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.membersRecyclerView.adapter = adapter
        binding.membersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isScrolling = true
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.membersListLiveData.value?.status != Status.LOADING
                ) {
                    viewModel.membersListLiveData.value?.let {
                        if (!it.isLastPage && isScrolling) {
                            viewModel.loadMore()
                            isScrolling = false
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
    }

    private fun subscribeUi() {
        viewModel.membersListLiveData.observe(viewLifecycleOwner, { result ->
            if (result.status == Status.SUCCESS && !result.data.isNullOrEmpty()) {
                adapter.submitList(result.data)
            }
        })
    }
}