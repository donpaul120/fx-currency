package com.fx.exchange_experiment.core.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyClientInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if(request.url.queryParameter("access_key") == null) {
            val queryBuilder = request.url.newBuilder()
                .addQueryParameter("access_key", "0e8a1451db1afb4ede1b4557f6edfe46")
                .build()
            request = request.newBuilder().url(queryBuilder).build()
        }
        return chain.proceed(request)
    }
}
