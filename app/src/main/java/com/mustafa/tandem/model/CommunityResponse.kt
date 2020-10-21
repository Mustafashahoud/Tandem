package com.mustafa.tandem.model

import com.google.gson.annotations.SerializedName

data class CommunityResponse(
    val errorCode: Any?,
    @SerializedName("response")
    val members: List<Member>,
    val type: String
)