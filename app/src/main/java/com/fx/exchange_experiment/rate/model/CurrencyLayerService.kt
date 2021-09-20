package com.fx.exchange_experiment.rate.model

import com.fx.exchange_experiment.core.network.NetworkResponse
import com.fx.exchange_experiment.rate.model.data.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface CurrencyLayerService {

    @GET("live")
    fun getRates(): Flow<NetworkResponse<ExchangeRateResponse>>

}