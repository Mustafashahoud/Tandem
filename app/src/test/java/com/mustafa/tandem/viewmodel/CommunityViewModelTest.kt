package com.mustafa.tandem.viewmodel

import MockTestUtil
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mustafa.tandem.MainCoroutinesRule
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.model.Status
import com.mustafa.tandem.repository.CommunityRepository
import com.mustafa.tandem.view.CommunityViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt


@ExperimentalCoroutinesApi
class CommunityViewModelTest {

    private lateinit var viewModel: CommunityViewModel
    private val repository: CommunityRepository = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        viewModel = CommunityViewModel(repository, coroutinesRule.testDispatcher)
    }

    @Test
    fun loadMoreTest() = coroutinesRule.testDispatcher.runBlockingTest {

        val observer = mock<Observer<Resource<List<Member>>>>()

        val members = MockTestUtil.createMembers(20)
        val resourceSuccess = Resource.success(members, false)

        val flow = flow {
            emit(resourceSuccess)
        }
        whenever(repository.getCommunityMembers(anyInt())).thenReturn(flow)

        viewModel.membersListLiveData.observeForever(observer)

        viewModel.loadMore()

        // Once as I am changing pageLiveData with pageLiveData.postValue(1) in init block
        verify(repository).getCommunityMembers(1)

        // The second call because of loadMore() with page 2
        verify(repository).getCommunityMembers(2)

        verify(observer, times(2)).onChanged(resourceSuccess)

        assertEquals(members, viewModel.membersListLiveData.value?.data)

        viewModel.membersListLiveData.removeObserver(observer)
    }

    @Test
    fun simpleLoadStatus() = coroutinesRule.testDispatcher.runBlockingTest {
        val observer = mock<Observer<Resource<List<Member>>>>()

        val resourceLoading = Resource.loading(null)

        val flow = flow {
            emit(resourceLoading)
        }

        whenever(repository.getCommunityMembers(anyInt())).thenReturn(flow)

        viewModel.membersListLiveData.observeForever(observer)

        verify(repository).getCommunityMembers(1)

        verify(observer).onChanged(resourceLoading)

        assertThat(viewModel.membersListLiveData.value?.status, `is`(Status.LOADING))
        assertThat(viewModel.membersListLiveData.value?.data, `is`(nullValue()))

        viewModel.membersListLiveData.removeObserver(observer)

    }

    @Test
    fun getCommunityMembersSuccessTest() = coroutinesRule.testDispatcher.runBlockingTest {
        val observer = mock<Observer<Resource<List<Member>>>>()

        val members = MockTestUtil.createMembers(20)
        val resourceSuccess = Resource.success(members, false)

        val flow = flow {
            emit(resourceSuccess)
        }
        whenever(repository.getCommunityMembers(anyInt())).thenReturn(flow)

        viewModel.membersListLiveData.observeForever(observer)

        // I am changing pageLiveData with pageLiveData.postValue(1) in init block
        verify(repository).getCommunityMembers(1)

        verify(observer).onChanged(resourceSuccess)

        assertEquals(members.size, viewModel.membersListLiveData.value?.data?.size)
        assertEquals(
            members[0].firstName,
            viewModel.membersListLiveData.value?.data?.get(0)?.firstName
        )
        assertEquals(members, viewModel.membersListLiveData.value?.data)

        // Or resourceSuccess.status
        assertThat(viewModel.membersListLiveData.value?.status, `is`(Status.SUCCESS))
        assertThat(viewModel.membersListLiveData.value, `is`(resourceSuccess))

        viewModel.membersListLiveData.removeObserver(observer)
    }


    @Test
    fun getCommunityMembersErrorTest() = coroutinesRule.testDispatcher.runBlockingTest {
        val observer = mock<Observer<Resource<List<Member>>>>()
        val errorMessage = "error"
        val resourceError = Resource.error(errorMessage, null)

        val flow = flow {
            emit(resourceError)
        }
        whenever(repository.getCommunityMembers(anyInt())).thenReturn(flow)

        viewModel.membersListLiveData.observeForever(observer)

        // I am changing pageLiveData with pageLiveData.postValue(1) in init block
        verify(repository).getCommunityMembers(1)

        verify(observer).onChanged(resourceError)

        // Or resourceError.status
        assertThat(viewModel.membersListLiveData.value?.status, `is`(Status.ERROR))
        assertThat(viewModel.membersListLiveData.value?.data, `is`(nullValue()))
        assertThat(viewModel.membersListLiveData.value?.message, `is`(errorMessage))

        viewModel.membersListLiveData.removeObserver(observer)

    }

}

