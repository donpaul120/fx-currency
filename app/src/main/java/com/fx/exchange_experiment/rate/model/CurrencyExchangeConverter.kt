package com.fx.exchange_experiment.rate.model

import com.fx.exchange_experiment.rate.model.data.Currency
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate
import javax.inject.Inject

class CurrencyExchangeConverter @Inject constructor(){

    /**
     * calculates the exchanges
     */
    fun convertToCurrencyRate(
        source: Currency = Currency.USD,
        baseCurrency: Currency,
        sourceRates: List<ExchangeRate>):List<ExchangeRate> {

        if(source.code == baseCurrency.code) return sourceRates

        val sourceToBaseRate = sourceRates.find {
            it.baseCurrency.code == source.code
                    && it.quoteCurrency.code == baseCurrency.code
        } ?: return listOf()

        return sourceRates.map {
            ExchangeRate(
                baseCurrency = baseCurrency,
                quoteCurrency = it.quoteCurrency,
                rate = it.rate / sourceToBaseRate.rate
            )
        }
    }

}