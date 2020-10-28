package com.mustafa.tandem.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mustafa.tandem.MainCoroutinesRule
import com.mustafa.tandem.api.ApiResponse
import com.mustafa.tandem.api.ApiUtil.createCall
import com.mustafa.tandem.api.ApiUtil.successCall
import com.mustafa.tandem.api.TandemService
import com.mustafa.tandem.model.CommunityResponse
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.model.Status
import com.mustafa.tandem.room.MemberDao
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class CommunityRepositoryTest {

    // Subject under test
    private lateinit var repository: CommunityRepository

    private val service = mock<TandemService>()

    private val memberDao = mock<MemberDao>()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        repository = CommunityRepository(service, memberDao, coroutinesRule.testDispatcher)
    }

    @Test
    fun getCommunityMembers_onlyOneMember() = coroutinesRule.testDispatcher.runBlockingTest {

        val mockMember =
            Member(1, 1, "Mustafa", listOf("SP", "HU"), listOf("EN", "DE"), "URL", 1, "TOPIC")
        val members = listOf(mockMember)
        val mockResponse = CommunityResponse(null, members, "success")
        val mockData = mockResponse.members

        val call = successCall(mockResponse)
        whenever(service.getCommunityMembers(1)).thenReturn(call)

        whenever(memberDao.loadMembersByPages(listOf(1))).thenReturn(members)

        repository.getCommunityMembers(1).collectIndexed { index, resource ->
            if (index == 0) assertThat(resource.status, `is`(Status.LOADING))
            if (index == 1) {
                assertThat(resource.status, `is`(Status.SUCCESS))
                resource.data?.let { members ->
                    assertThat(members[0].firstName, `is`("Mustafa"))
                    assertThat(members[0].topic, `is`("TOPIC"))
                    assertThat(members[0].referenceCnt, `is`(1))
                    assertThat(members[0].learns[0], `is`("SP"))
                    assertThat(members[0].natives[0], `is`("EN"))
                    assertThat(members[0].pictureUrl, `is`("URL"))
                    assertThat(members, `is`(mockData))
                }
                assertThat(resource.message, `is`(nullValue()))
                // Since we have only one members, when the count of the response members is less than 20, that means we are in the last page
                assertThat(resource.isLastPage, `is`(true))
            }

        }
        verify(service, times(1)).getCommunityMembers(1)
        verifyNoMoreInteractions(service)

    }

    @Test
    fun getCommunityMembers_20Members() = coroutinesRule.testDispatcher.runBlockingTest {

        val mockMembers = MockTestUtil.createMembers(20)
        val mockResponse = CommunityResponse(null, mockMembers, "success")

        val call = successCall(mockResponse)
        whenever(service.getCommunityMembers(1)).thenReturn(call)

        whenever(memberDao.loadMembersByPages(listOf(1))).thenReturn(mockMembers)

        repository.getCommunityMembers(1).collectIndexed { index, resource ->
            if (index == 0) assertThat(resource.status, `is`(Status.LOADING))
            if (index == 1) {
                assertThat(resource.status, `is`(Status.SUCCESS))
                assertThat(resource.data?.size, `is`(20))
                assertThat(resource.message, `is`(nullValue()))
                // Since we have 20 members, it is not the last page
                assertThat(resource.isLastPage, `is`(false))
            }

        }
        verify(service, times(1)).getCommunityMembers(1)
        verifyNoMoreInteractions(service)

    }

    @Test
    fun exceptionFromNetwork() = coroutinesRule.testDispatcher.runBlockingTest {
        val exception = Exception("foo")
        val apiResponse = ApiResponse.exception<CommunityResponse>(exception)
        whenever(service.getCommunityMembers(1)).thenReturn(apiResponse)

        val flow = repository.getCommunityMembers(1)

        // We have two emits LOADING and ERROR (Exception)
        assertThat(flow.count(), `is`(2))

        // List to keep weather forecast values
        val membersList = mutableListOf<Resource<List<Member>>>()

        // Convert the Flow to list
        flow.toList(membersList)

        assertThat(membersList.first().status, `is`(Status.LOADING))
        assertThat(membersList.first().data, `is`(nullValue()))

        assertThat(membersList.last().status, `is`(Status.ERROR))
        assertThat(membersList.last().data, `is`(nullValue()))

    }

    @Test
    fun errorFromNetwork() = coroutinesRule.testDispatcher.runBlockingTest {
        val body = "error".toResponseBody("text/html".toMediaTypeOrNull())

        val failureCall = createCall(Response.error<CommunityResponse>(500, body))
        whenever(service.getCommunityMembers(1)).thenReturn(failureCall)

        val flow = repository.getCommunityMembers(1)

        // We have two emits LOADING and ERROR
        assertThat(flow.count(), `is`(2))

        // Use the collectIndexed Operator
        flow.collectIndexed { index, value ->
            if (index == 0) assertThat(value.status, `is`(Status.LOADING))
            if (index == 1) {
                assertThat(value.data, `is`(nullValue()))
                assertThat(value.message, containsString("error"))
                assertThat(value.status, `is`(Status.ERROR))
            }
        }
    }
}
