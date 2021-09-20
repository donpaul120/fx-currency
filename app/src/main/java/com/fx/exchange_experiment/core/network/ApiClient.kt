package com.fx.exchange_experiment.core.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.lang.reflect.Type

class ApiClient {

    private lateinit var adapterBuilder: Retrofit.Builder
    private var okBuilder: OkHttpClient.Builder? = null
    private var json: GsonBuilder? = null
    private val apiAuthorizations = LinkedHashMap<String, Interceptor>()

    init {
        createDefaultAdapter()
    }

    fun addAuthorization(authName: String, authorization: Interceptor): ApiClient {
        if (apiAuthorizations.containsKey(authName)) {
            throw RuntimeException("auth name \"$authName\" already in api authorizations")
        }
        apiAuthorizations[authName] = authorization
        okBuilder!!.addInterceptor(authorization)
        return this
    }

    private fun createDefaultAdapter() {
        json = GsonBuilder()
        okBuilder = OkHttpClient.Builder()
        adapterBuilder = Retrofit.Builder()
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .addConverterFactory(
               GsonCustomConverterFactory.create(json?.create())
            )
    }

    fun <S> createService(serviceClass: Class<S>, endpoint: String): S {
        var baseUrl = endpoint
        if (!baseUrl.endsWith("/")) baseUrl = "$baseUrl/"
        return adapterBuilder
            .client(okBuilder!!.build())
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }


    //Reusable Code Below @paul

    /**
     * This wrapper is to take care of this case:
     * when the deserialization fails due to JsonParseException and the
     * expected type is String, then just return the body string.
     */
    internal class GsonResponseBodyConverterToString<T>(
        private val gson: Gson,
        private val type: Type
    ) : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val returned = value.string()
            return try {
                gson.fromJson(returned, type)
            } catch (e: JsonParseException) {
                returned as T
            }
        }
    }

    //General ConverterFactory
    internal class GsonCustomConverterFactory private constructor(gson: Gson?) : Converter.Factory() {

        private val gson: Gson
        private val gsonConverterFactory: GsonConverterFactory
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            return if (type == String::class.java) GsonResponseBodyConverterToString<Any>(
                gson,
                type
            ) else gsonConverterFactory.responseBodyConverter(type, annotations, retrofit)
        }

        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            return gsonConverterFactory.requestBodyConverter(
                type,
                parameterAnnotations,
                methodAnnotations,
                retrofit
            )
        }

        companion object {
            fun create(gson: Gson?): GsonCustomConverterFactory {
                return GsonCustomConverterFactory(gson)
            }
        }

        init {
            if (gson == null) throw NullPointerException("gson == null")
            this.gson = gson
            gsonConverterFactory = GsonConverterFactory.create(gson)
        }
    }
}