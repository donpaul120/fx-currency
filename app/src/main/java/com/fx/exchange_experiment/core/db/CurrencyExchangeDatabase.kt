package com.fx.exchange_experiment.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate
import com.fx.exchange_experiment.rate.model.dao.ExchangeRateDao

@Database(
    version = 1,
    entities = [
        ExchangeRate::class
    ],
    exportSchema = false
)
@TypeConverters(value = [DbTypeConverters::class])
abstract class CurrencyExchangeDatabase : RoomDatabase() {
    abstract fun getExchangeRateDao(): ExchangeRateDao
}