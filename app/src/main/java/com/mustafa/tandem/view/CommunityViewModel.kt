package com.mustafa.tandem.view


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafa.tandem.repository.CommunityRepository
import javax.inject.Inject

class CommunityViewModel @Inject constructor(
    repository: CommunityRepository,
) : ViewModel() {

    val membersStream = repository.getMembersStream().cachedIn(viewModelScope)
}
