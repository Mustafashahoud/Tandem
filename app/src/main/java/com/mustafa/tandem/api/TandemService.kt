package com.mustafa.tandem.api

import com.mustafa.tandem.model.CommunityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TandemService {

    companion object {
        const val ENDPOINT = "https://tandem2019.web.app/"
    }

    @GET("/api/community_{page}.json")
    suspend fun getCommunityMembers(@Path("page") page: Int): CommunityResponse
}