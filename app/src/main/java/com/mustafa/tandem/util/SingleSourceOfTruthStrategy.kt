//package com.mustafa.tandem.util
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.liveData
//import androidx.lifecycle.map
//import com.mustafa.tandem.model.Resource
//import com.mustafa.tandem.model.Status
//import kotlinx.coroutines.Dispatchers
//
///**
// * The database serves as the single source of truth.
// * Therefore UI can receive data updates from database only.
// * Function notify UI about:
// * [Status.SUCCESS] - with data from database
// * [Status.ERROR] - if error has occurred from any source
// * [Status.LOADING]
// */
//fun <T, A> resultLiveData(databaseQuery: () -> LiveData<T>,
//                          networkCall: suspend () -> Resource<A>,
//                          saveCallResult: suspend (A) -> Unit,
//                          hasNextPage: Boolean): LiveData<Resource<T>> =
//        liveData(Dispatchers.IO) {
//            emit(Resource.loading<T>())
//            val source = databaseQuery.invoke().map { Resource.success(it, hasNextPage) }
//            emitSource(source)
//
//            val responseStatus = networkCall.invoke()
//            if (responseStatus.status == Resource.Status.SUCCESS) {
//                saveCallResult(responseStatus.data!!)
//            } else if (responseStatus.status == Resource.Status.ERROR) {
//                emit(Resource.error(responseStatus.message!!))
//                emitSource(source)
//            }
//        }