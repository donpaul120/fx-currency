package com.fx.exchange_experiment.core.network

sealed class NetworkResponse<out T> {
    data class Success<T>(val body: T?) : NetworkResponse<T>()
    data class Error<T>(val serviceError: ServiceError, val data: T? = null) : NetworkResponse<T>()
    data class Empty<T> (val data: T? = null) : NetworkResponse<T>()
}

data class ServiceError(
    val code: String,
    val message: String
)