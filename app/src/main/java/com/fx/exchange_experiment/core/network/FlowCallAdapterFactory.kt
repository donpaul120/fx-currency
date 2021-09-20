package com.fx.exchange_experiment.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.awaitResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowCallAdapterFactory private constructor() : CallAdapter.Factory() {

    companion object {
        fun create() = FlowCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Flow::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "Deferred return type must be parameterized as Deferred<Foo> or Deferred<out Foo>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawDeferredType = getRawType(responseType)

        return if (rawDeferredType == NetworkResponse::class.java) {
            check(responseType is ParameterizedType) {
                "Response must be parameterized as Response<Foo> or Response<out Foo>"
            }

            val apiResult = getParameterUpperBound(0, responseType)
            FlowApiResponseCallAdapter<Any>(apiResult)
        } else {
            null
        }
    }

    private class FlowApiResponseCallAdapter<T>(
        private val responseType: Type,
    ) : CallAdapter<T, Flow<NetworkResponse<T>>> {
        override fun responseType(): Type = responseType

        override fun adapt(call: Call<T>): Flow<NetworkResponse<T>> = flow {
            emit(NetworkResponseHandler.handle(call.awaitResponse()))
        }.catch { e ->
            emit(NetworkResponseHandler.handle(e))
        }
    }
}