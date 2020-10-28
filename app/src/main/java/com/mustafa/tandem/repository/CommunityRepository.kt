package com.mustafa.tandem.repository

import com.mustafa.tandem.api.TandemService
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.room.MemberDao
import com.mustafa.tandem.testing.OpenForTesting
import com.mustafa.tandem.util.networkBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class CommunityRepository @Inject constructor(
    private val service: TandemService,
    private val memberDao: MemberDao,
    private val dispatcherIO: CoroutineDispatcher
) {

    suspend fun getCommunityMembers(page: Int): Flow<Resource<List<Member>>> {
        return networkBoundResource(
            loadFromDb = {
                memberDao.loadMembersByPages((1..page).toList())
            },
            fetchFromNetwork = { service.getCommunityMembers(page) },
            dispatcherIO = dispatcherIO,
            moviePagingChecker = {
                it.members.size < 20
            },
            saveFetchResult = { items ->
                items.members.forEach { it.page = page }
                items.members.forEach {
                    it.id = memberDao.hashPrimary(
                        it.firstName,
                        it.referenceCnt,
                        it.page,
                        it.pictureUrl
                    )
                }
                memberDao.insertMemberList(members = items.members)
            }
        )
    }
}