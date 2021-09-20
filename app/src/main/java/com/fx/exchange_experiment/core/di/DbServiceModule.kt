package com.fx.exchange_experiment.core.di

import android.content.Context
import androidx.room.Room
import com.fx.exchange_experiment.BuildConfig
import com.fx.exchange_experiment.core.db.CurrencyExchangeDatabase
import com.fx.exchange_experiment.rate.model.dao.ExchangeRateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbServiceModule {

    @Provides
    @Singleton
    fun getApplicationDatabase(@ApplicationContext context: Context): CurrencyExchangeDatabase {
        return Room.databaseBuilder(context, CurrencyExchangeDatabase::class.java, BuildConfig.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun getExchangeRateDao(database: CurrencyExchangeDatabase): ExchangeRateDao {
        return database.getExchangeRateDao()
    }

}