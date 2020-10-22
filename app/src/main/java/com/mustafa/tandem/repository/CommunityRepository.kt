package com.mustafa.tandem.repository

import androidx.annotation.WorkerThread
import com.mustafa.tandem.api.*
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.testing.OpenForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class CommunityRepository @Inject constructor(
    private val service: TandemService,
    private val dispatcherIO: CoroutineDispatcher
) {

    @WorkerThread
    suspend fun getCommunityMembers(
        page: Int
    ): Flow<Resource<List<Member>>> {
        return flow {
            emit(Resource.loading(null))
            service.getCommunityMembers(page).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.success(it.members, it.members.size != 20))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(Resource.error(message(), null))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                emit(Resource.error(message(), null))
            }

        }.flowOn(dispatcherIO)

    }
}