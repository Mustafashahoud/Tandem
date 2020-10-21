package com.mustafa.tandem.api

import com.mustafa.tandem.MainCoroutinesRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class TandemServiceTest : ApiAbstract<TandemService>() {

    private lateinit var service: TandemService

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @Before
    fun initService() {
        this.service = createService(TandemService::class.java)
    }

    @Throws(IOException::class)
    @Test
    fun fetchMembers() = runBlocking {
        enqueueResponse("/members.json")
        val response = service.getCommunityMembers(1)
        val responseBody = requireNotNull((response as ApiResponse.ApiSuccessResponse).data)
        mockWebServer.takeRequest()

        assertThat(responseBody.type, `is`("success"))
        assertThat(responseBody.members[0].firstName, `is`("Mustafa"))
        assertThat(responseBody.members[0].pictureUrl, `is`("/URL1.png"))
        assertThat(responseBody.members[0].referenceCnt, CoreMatchers.`is`(0))
        assertThat(responseBody.members[0].topic, `is`("topic1"))
        assertThat(responseBody.members[1].firstName, `is`("Mark"))
        assertThat(responseBody.members[1].pictureUrl, `is`("/URL2.png"))
        assertThat(responseBody.members[1].referenceCnt, `is`(1))
        assertThat(responseBody.members[1].topic, `is`("topic2"))
    }
}

