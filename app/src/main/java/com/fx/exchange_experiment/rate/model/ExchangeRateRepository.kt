package com.fx.exchange_experiment.rate.model

import com.fx.exchange_experiment.core.network.NetworkResponse
import com.fx.exchange_experiment.core.network.Resource
import com.fx.exchange_experiment.core.network.networkBoundResource
import com.fx.exchange_experiment.rate.model.dao.ExchangeRateDao
import com.fx.exchange_experiment.rate.model.data.Currency
import com.fx.exchange_experiment.rate.model.data.ExchangeRateResponse
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeRateRepository
@Inject  constructor(
    private val exchangeDao: ExchangeRateDao,
    private val currencyLayerService: CurrencyLayerService,
    private val exchangeConverter: CurrencyExchangeConverter
) {

    /**
     *
     */
    fun getExchangeRatesForCurrency(
        base: Currency = Currency.USD,
    ): Flow<Resource<List<ExchangeRate>>> {
        return networkBoundResource(
            shouldFetchFromRemote = { false },
            fetchFromLocal = {
                exchangeDao.getExchangeRates().map {
                    return@map exchangeConverter.convertToCurrencyRate(
                        baseCurrency = base,
                        source = Currency.USD,
                        sourceRates = it
                    )
                }
            },
            fetchFromRemote = { currencyLayerService.getRates().map(::convertApiResponseToList) },
            saveRemoteData = {}
        ).flowOn(Dispatchers.IO)
    }

    /**
     *
     */
    fun getCurrencyRates(): Flow<Resource<List<ExchangeRate>>> {
        println("Starting Call....")
        return networkBoundResource(
            fetchFromLocal = {  exchangeDao.getExchangeRates() },
            fetchFromRemote = { currencyLayerService.getRates().map(::convertApiResponseToList) },
            saveRemoteData = { exchangeDao.insertItems(*it.toTypedArray()) }
        ).flowOn(Dispatchers.IO)
    }

    private fun convertApiResponseToList(
        response: NetworkResponse<ExchangeRateResponse>):NetworkResponse<List<ExchangeRate>> {
        return when(response) {
            is NetworkResponse.Error -> NetworkResponse.Error(response.serviceError)
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toExchangeRates())
            else -> NetworkResponse.Success(listOf())
        }
    }

}