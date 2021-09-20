package com.fx.exchange_experiment.rate.model.worker

import android.content.Context
import androidx.annotation.NonNull
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fx.exchange_experiment.core.network.Resource
import com.fx.exchange_experiment.rate.model.ExchangeRateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

@HiltWorker
class PeriodicExchangeRateWorker @AssistedInject constructor(
    @Assisted @NonNull context: Context,
    @Assisted @NonNull parameters: WorkerParameters,
    val repository: ExchangeRateRepository,
): CoroutineWorker(context, parameters){

    override suspend fun doWork(): Result {
        return when(val currencyRates = repository.getCurrencyRates().drop(2).first()) {
            is Resource.Error<*> -> Result.failure()
            is Resource.Success<*> -> Result.success()
            else -> {
                println(currencyRates)
                Result.retry()
            }
        }
    }

}
