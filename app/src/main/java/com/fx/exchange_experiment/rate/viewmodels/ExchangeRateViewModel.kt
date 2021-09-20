package com.fx.exchange_experiment.rate.viewmodels

import androidx.lifecycle.ViewModel
import com.fx.exchange_experiment.core.network.Resource
import com.fx.exchange_experiment.rate.model.ExchangeRateRepository
import com.fx.exchange_experiment.rate.model.data.Currency
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    var selectedCurrency: Currency = Currency.USD
    var exchangeAmount: Double = 1.00

    fun getExchangeRates(): Flow<Resource<List<ExchangeRate>>> {
        return exchangeRateRepository.getExchangeRatesForCurrency(selectedCurrency)
    }

}