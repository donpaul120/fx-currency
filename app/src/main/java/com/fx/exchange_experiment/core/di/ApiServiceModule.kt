package com.fx.exchange_experiment.core.di

import com.fx.exchange_experiment.core.network.ApiClient
import com.fx.exchange_experiment.core.network.ApiKeyClientInterceptor
import com.fx.exchange_experiment.rate.model.CurrencyLayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    private const val baseApi = "http://api.currencylayer.com/"

    @Provides
    fun provideApiClient() : ApiClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return ApiClient().apply {
            addAuthorization("auth", ApiKeyClientInterceptor())
            addAuthorization("logging", loggingInterceptor)
        }
    }

    @Provides
    @Singleton
    fun getCurrencyLayerService(apiClient: ApiClient) : CurrencyLayerService =
        apiClient.createService(CurrencyLayerService::class.java, baseApi)

}