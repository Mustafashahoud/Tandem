package com.mustafa.tandem.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "members")
data class Member(
    @PrimaryKey
    var id: Int,
    val firstName: String,
    val learns: List<String>,
    val natives: List<String>,
    val pictureUrl: String,
    val referenceCnt: Int,
    val topic: String,
)