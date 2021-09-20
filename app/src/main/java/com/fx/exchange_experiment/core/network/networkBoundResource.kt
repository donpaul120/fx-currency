package com.fx.exchange_experiment.core.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * @author Paul Okeke
 */
@ExperimentalCoroutinesApi
inline fun <DB, REMOTE> networkBoundResource(
    crossinline fetchFromLocal: () -> Flow<DB>,
    crossinline shouldFetchFromRemote: (DB?) -> Boolean = { true },
    crossinline fetchFromRemote: () -> Flow<NetworkResponse<REMOTE>>,
    crossinline processRemoteResponse: (response: NetworkResponse.Success<REMOTE>) -> Unit = { Unit },
    crossinline saveRemoteData: suspend (REMOTE) -> Unit = { Unit },
    crossinline onFetchFailed: (errorBody: ServiceError?, statusCode: Int) -> Unit = { _: ServiceError?, _: Int -> Unit }
) = flow<Resource<DB>> {

    emit(Resource.Loading(null))

    val localData = fetchFromLocal().first()

    if (shouldFetchFromRemote(localData)) {

        emit(Resource.Loading(localData))

        fetchFromRemote().collect { apiResponse ->

            when (apiResponse) {

                is NetworkResponse.Success -> {
                    processRemoteResponse(apiResponse)
                    apiResponse.body?.let { saveRemoteData(it) }
                    emitAll(fetchFromLocal().map { dbData ->
                        Resource.Success(dbData!!)
                    })
                }

                is NetworkResponse.Error -> {
                    if(apiResponse.serviceError.code == "401" || apiResponse.serviceError.message == "401") {
                        return@collect
                    }
                    onFetchFailed(apiResponse.serviceError, apiResponse.serviceError.code.toIntOrNull()?:0)
                    emitAll(fetchFromLocal().map {
                        Resource.Error(apiResponse.serviceError, it)
                    })
                }
                else -> Unit
            }
        }
    } else {
        emitAll(fetchFromLocal().map { Resource.Success(it!!) })
    }
}