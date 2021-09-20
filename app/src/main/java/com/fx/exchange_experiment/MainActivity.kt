package com.fx.exchange_experiment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.work.*
import com.fx.exchange_experiment.rate.model.worker.PeriodicExchangeRateWorker
import com.fx.exchange_experiment.rate.viewmodels.ExchangeRateViewModel
import com.fx.exchange_experiment.rate.views.fragments.CurrencyExchangeFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Suppress("unused")
    private val viewModel: ExchangeRateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()

        registerWorkers()
    }

    private fun registerWorkers() {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<PeriodicExchangeRateWorker>(
            30, TimeUnit.MINUTES
        ).setConstraints(workConstraints).build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "periodic_exchange_rates_update",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    private fun setupView() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, CurrencyExchangeFragment.newInstance())
        transaction.commit()
    }
}