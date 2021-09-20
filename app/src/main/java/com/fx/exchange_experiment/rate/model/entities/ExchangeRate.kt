package com.fx.exchange_experiment.rate.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.fx.exchange_experiment.rate.model.data.Currency

@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["base", "quote"]
)
data class ExchangeRate (
    @ColumnInfo(name = "base")
    val baseCurrency: Currency,
    @ColumnInfo(name = "quote")
    val quoteCurrency: Currency,
    val rate: Double, //1 Base will cost how many quote
)