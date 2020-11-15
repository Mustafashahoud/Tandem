package com.mustafa.tandem.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mustafa.tandem.api.TandemService
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.RemoteKeys
import com.mustafa.tandem.room.AppDatabase
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException


@OptIn(ExperimentalPagingApi::class)
/**
 * @param service  so we can make network requests.
 * @param database so we can save data we got from the network request.
 */
class MembersRemoteMediator(
    private val service: TandemService,
    private val database: AppDatabase
) : RemoteMediator<Int, Member>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Member>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: NEWS_API_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                if (remoteKeys == null) {
                    // The LoadType is PREPEND so some data was loaded before,
                    // so we should have been able to get remote keys
                    // If the remoteKeys are null, then we're an invalid state and we have a bug
                    throw InvalidObjectException("Remote key and the prevKey should not be null")
                }
                // If the previous key is null, then we can't request more data
                val prevKey = remoteKeys.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys == null || remoteKeys.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextKey
            }

        }
        try {
            val response = service.getCommunityMembers(page)

            val members = response.members
            members.forEachIndexed { index, member ->
                // Just a function to generate ids .. autoGenerate does not help in my case
                member.id = (index + 1) + ((page - 1 ) * NEW_API_PAGE_SIZE)
            }

            val endOfPaginationReached = members.isEmpty() || members.size < NEW_API_PAGE_SIZE
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.memberDao().clearMembers()
                }

                val prevKey = if (page == NEWS_API_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = members.map {
                    RemoteKeys(memberId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.memberDao().insertAll(members)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    companion object {
        private const val NEWS_API_STARTING_PAGE_INDEX = 1
        private const val NEW_API_PAGE_SIZE = 20
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Member>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { member ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao().remoteKeysMemberId(member.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Member>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { member ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysMemberId(member.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Member>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { memberId ->
                database.remoteKeysDao().remoteKeysMemberId(memberId)
            }
        }
    }

}