package com.mustafa.tandem.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mustafa.tandem.model.Member


@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<Member>)

    @Query("SELECT * FROM members ORDER BY id ASC ")
    fun queryMembers(): PagingSource<Int, Member>

    @Query("DELETE FROM members")
    suspend fun clearMembers()
}