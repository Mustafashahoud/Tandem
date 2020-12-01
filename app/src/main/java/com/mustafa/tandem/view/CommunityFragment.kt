package com.mustafa.tandem.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.mustafa.tandem.R
import com.mustafa.tandem.databinding.CommunityFragmentBinding
import com.mustafa.tandem.di.Injectable
import com.mustafa.tandem.util.autoCleared
import com.mustafa.tandem.view.adapter.LoadStateAdapter
import com.mustafa.tandem.view.adapter.MembersAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityFragment : Fragment(R.layout.community_fragment), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: CommunityFragmentBinding? = null
    private val binding get() = _binding!!

    private var pagingAdapter by autoCleared<MembersAdapter>()

    private val viewModel by viewModels<CommunityViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = CommunityFragmentBinding.bind(view)

        binding.retry.setOnClickListener { pagingAdapter.retry() }
        initAdapter()
        subscribeUI()

    }

    private fun subscribeUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.membersStream.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    private fun initAdapter() {
        /**
         * MyAdapter adapter1 = ...;
         * AnotherAdapter adapter2 = ...;
         * ConcatAdapter concatenated = new ConcatAdapter(adapter1, adapter2);
         * recyclerView.setAdapter(concatenated);
         */
        pagingAdapter = MembersAdapter()
        binding.membersRecyclerView.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { pagingAdapter.retry() },
            footer = LoadStateAdapter { pagingAdapter.retry() }
        )

        pagingAdapter.addLoadStateListener { loadState ->
            binding.membersRecyclerView.isVisible = loadState.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            binding.retry.isVisible = loadState.refresh is LoadState.Error
            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // This callback notifies us every time there's a change in the load state via a CombinedLoadStates object.
        // CombinedLoadStates gives us the load state for the PageSource
        // Or it gives us the load state for RemoteMediator needed for network and database case
//        pagingAdapter.addLoadStateListener { loadState ->
//            // Only show the list if refresh succeeds.
//            binding.membersRecyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading // Not Loading and Not Error -> Success
//            // Show loading spinner during initial load or refresh.
//            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
//            // Show the retry state if initial load or refresh fails.
//            binding.retry.isVisible = loadState.source.refresh is LoadState.Error
//
//            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
//            val errorState = loadState.source.append as? LoadState.Error
//                ?: loadState.source.prepend as? LoadState.Error
//                ?: loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//            errorState?.let {
//                Toast.makeText(
//                    requireContext(),
//                    "\uD83D\uDE28 Wooops ${it.error}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}