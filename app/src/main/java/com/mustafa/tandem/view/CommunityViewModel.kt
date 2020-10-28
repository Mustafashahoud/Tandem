package com.mustafa.tandem.view


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.tandem.repository.CommunityRepository
import com.mustafa.tandem.testing.OpenForTesting
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@OpenForTesting
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository,
    dispatcherIO: CoroutineDispatcher
) :
    ViewModelBase(dispatcherIO) {

    private val pageLiveData: MutableLiveData<Int> = MutableLiveData()

    private var pageNumber = 1

    init {
        pageLiveData.postValue(1)
    }


    val membersListLiveData = pageLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.getCommunityMembers(pageNumber).asLiveData()
        }
    }


    fun loadMore() {
        pageNumber++
        pageLiveData.postValue(pageNumber)
    }

    fun refresh() {
        pageLiveData.value?.let {
            pageLiveData.value = it
        }
    }
}
