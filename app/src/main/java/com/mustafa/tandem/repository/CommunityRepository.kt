package com.mustafa.tandem.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mustafa.tandem.api.TandemService
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.room.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommunityRepository @Inject constructor(
    private val service: TandemService,
    private val database: AppDatabase
) {

    fun getMembersStream(): LiveData<PagingData<Member>> {
        val pagingSourceFactory = { database.memberDao().queryMembers() }

        return Pager(
            PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MembersRemoteMediator(service, database),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    companion object {
        // Depending on the backend .. in my case it is 20
        private const val NETWORK_PAGE_SIZE = 10
    }
}