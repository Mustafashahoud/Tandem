package com.mustafa.tandem.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mustafa.tandem.model.Member
import kotlin.math.abs


@Dao
abstract class MemberDao {

    @Query("SELECT * FROM Member WHERE page in (:pages) order by page")
    abstract suspend fun loadMembersByPages(pages: List<Int>): List<Member>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMemberList(members: List<Member>)

    fun hashPrimary(firstName: String, refCnt: Int, page: Int, picUrl: String): Int {
        var hash = 7
        hash = 31 * hash + refCnt
        hash = 31 * hash + page
        hash = 31 * hash + firstName.hashCode()
        hash = 31 * hash + picUrl.hashCode()
        return abs(hash)

    }
}