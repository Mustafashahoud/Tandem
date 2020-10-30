package com.mustafa.tandem.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.tandem.model.Member
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemberDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndRead() = runBlocking {
        val mockMember =
            Member(
                1,
                1,
                "Mustafa",
                listOf("SP", "HU"),
                listOf("EN", "DE"),
                "URL",
                1,
                "TOPIC"
            )
        db.memberDao().insertMemberList(listOf(mockMember))

        val loaded = db.memberDao().loadMembersByPages(listOf(1))

        assertThat(loaded, notNullValue())
        assertThat(loaded[0].firstName, `is`("Mustafa"))
        assertThat(loaded[0].referenceCnt, `is`(1))
        assertThat(loaded[0].pictureUrl, `is`("URL"))
        assertThat(loaded[0].natives[0], `is`("EN"))
        assertThat(loaded[0].learns[0], `is`("SP"))
        assertThat(loaded[0].page, `is`(1))
        assertThat(loaded[0].topic, `is`("TOPIC"))
        assertThat(loaded[0].id, `is`(1))

    }
}