package com.mustafa.tandem.api

import com.nhaarman.mockitokotlin2.mock
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiUtil {

  fun <T : Any> successCall(data: T) = createCall(Response.success(data))

  fun <T : Any> createCall(response: Response<T>): ApiResponse<T> =
    ApiResponse.create(200..229, response)

}