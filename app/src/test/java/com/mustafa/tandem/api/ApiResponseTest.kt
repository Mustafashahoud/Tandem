package com.mustafa.tandem.api

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ApiResponseTest {

    @Test
    fun exception() {
        val exception = Exception("foo")
        val apiResponse = ApiResponse.exception<String>(exception)
        assertThat(apiResponse.message, `is`("foo"))
    }

    @Test
    fun success() {
        val apiResponse =
            ApiResponse.create(200..299, Response.success("foo"))
        if (apiResponse is ApiResponse.ApiSuccessResponse) {
            assertThat(apiResponse.data, `is`("foo"))
        }
    }
}
