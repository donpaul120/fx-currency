package com.fx.exchange_experiment.core.network

sealed class Resource<T> {
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error : ServiceError, val data: T? = null) : Resource<T>()
}
