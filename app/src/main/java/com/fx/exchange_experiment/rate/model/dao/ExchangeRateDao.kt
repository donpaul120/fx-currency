package com.fx.exchange_experiment.rate.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.fx.exchange_experiment.core.db.BaseDao
import com.fx.exchange_experiment.rate.model.entities.ExchangeRate
import kotlinx.coroutines.flow.Flow


@Dao
abstract class ExchangeRateDao : BaseDao<ExchangeRate>() {

    @Query("SELECT * FROM exchange_rates")
    abstract fun getExchangeRates(): Flow<List<ExchangeRate>>

}