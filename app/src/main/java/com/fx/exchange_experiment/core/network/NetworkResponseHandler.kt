package com.fx.exchange_experiment.core.network

import okio.IOException
import retrofit2.Response

object NetworkResponseHandler {

    fun <T> handle(response: Response<T>) : NetworkResponse<T> {
        return if (response.isSuccessful) {
            val body = response.body() ?: return NetworkResponse.Empty()
            NetworkResponse.Success(body)
        } else {
            val errorMessage = response.errorBody()?.string()
            NetworkResponse.Error(ServiceError("${response.code()}", errorMessage ?: ""))
        }
    }

    fun <T> handle(throwable: Throwable): NetworkResponse<T> {
        return when (throwable) {
            is IOException -> NetworkResponse.Error(ServiceError("", "No Internet Connection"))
            else -> NetworkResponse.Error(ServiceError("", "An unknown error occurred!"))
        }
    }

}