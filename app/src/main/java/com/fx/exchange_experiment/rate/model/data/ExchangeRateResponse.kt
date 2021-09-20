package com.fx.exchange_experiment.rate.model.data

import com.google.gson.annotations.SerializedName
import com.fx.exchange_experiment.core.network.ApiResult
import com.fx.exchange_experiment.core.utils.CurrencyUtil
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate

data class ExchangeRateResponse(
    @SerializedName("quotes")
    val quotes: HashMap<String, Number> = hashMapOf()
) : ApiResult() {

    fun toExchangeRates(): List<ExchangeRate> {
        val exchangeList = arrayListOf<ExchangeRate>()
        this.quotes.forEach {
            val pair = it.key
            if(pair.length != 6) return@forEach
            val baseString = pair.substring(0, 3)
            val quoteString = pair.substring(3, pair.length)

            val baseCurrency = CurrencyUtil.getCurrencyByCode(baseString)
            val quoteCurrency = CurrencyUtil.getCurrencyByCode(quoteString)

            if(baseCurrency != null && quoteCurrency != null) {
                exchangeList.add(
                    ExchangeRate(
                        baseCurrency = baseCurrency,
                        quoteCurrency = quoteCurrency,
                        rate = it.value.toDouble()
                   )
                )
            }
        }
        return exchangeList
    }
}
