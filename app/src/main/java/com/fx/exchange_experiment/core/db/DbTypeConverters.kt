package com.fx.exchange_experiment.core.db

import androidx.room.TypeConverter
import com.fx.exchange_experiment.core.utils.CurrencyUtil
import com.fx.exchange_experiment.rate.model.data.Currency

object DbTypeConverters {

    @TypeConverter
    fun currencyToString(currency: Currency) : String{
        return currency.code
    }

    @TypeConverter
    fun codeToCurrency(code : String) : Currency?{
        return CurrencyUtil.getCurrencyByCode(code)
    }

}