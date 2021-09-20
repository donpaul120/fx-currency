package com.fx.exchange_experiment.rate.model.data

import com.fx.exchange_experiment.rate.model.entities.ExchangeRate

data class ExchangeRateListItem(
    val exchange: ExchangeRate,
    val amount: Double
)
