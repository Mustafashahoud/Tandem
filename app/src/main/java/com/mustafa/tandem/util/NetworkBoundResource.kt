package com.mustafa.tandem.util

import com.mustafa.tandem.api.*
import com.mustafa.tandem.model.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Standalone Function For Single Source of Truth
 */
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline loadFromDb: () -> ResultType?,
    crossinline fetchFromNetwork: suspend () -> ApiResponse<RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    crossinline moviePagingChecker: (RequestType) -> Boolean = { true },
    dispatcherIO: CoroutineDispatcher
) = flow<Resource<ResultType>> {
    emit(Resource.loading(null))
    val data = loadFromDb()

    if (shouldFetch(data)) {
        fetchFromNetwork().apply {
            this.onSuccessSuspend {
                this.data?.let {
                    saveFetchResult(it)
                    emit(Resource.success(loadFromDb(), moviePagingChecker(it)))
                }

            }.onErrorSuspend {
                emit(Resource.error(message(), null))
            }.onExceptionSuspend {
                emit(Resource.error(message(), null))
            }
        }

    } else {
        Resource.success(data, true)
    }

}.flowOn(dispatcherIO)

