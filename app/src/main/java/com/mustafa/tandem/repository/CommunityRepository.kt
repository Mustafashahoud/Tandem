package com.mustafa.tandem.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mustafa.tandem.api.TandemService
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommunityRepository @Inject constructor(
    private val service: TandemService,
    private val database: AppDatabase
) {

    fun getMembersStream(): Flow<PagingData<Member>> {
        val pagingSourceFactory = { database.memberDao().queryMembers() }

        return Pager(
            PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = 40,
                prefetchDistance = 5,
                initialLoadSize = 40
            ),
            remoteMediator = MembersRemoteMediator(service, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        // Depending on the backend .. in my case it is 20
        private const val NETWORK_PAGE_SIZE = 20
    }
}