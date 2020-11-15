package com.mustafa.tandem.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    var memberId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)