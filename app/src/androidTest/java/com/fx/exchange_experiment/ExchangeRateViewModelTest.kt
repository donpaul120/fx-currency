package com.fx.exchange_experiment

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import app.cash.turbine.test
import com.fx.exchange_experiment.core.db.CurrencyExchangeDatabase
import com.fx.exchange_experiment.core.network.NetworkResponse
import com.fx.exchange_experiment.core.network.Resource
import com.fx.exchange_experiment.data.Faker
import com.fx.exchange_experiment.rate.model.CurrencyExchangeConverter
import com.fx.exchange_experiment.rate.model.CurrencyLayerService
import com.fx.exchange_experiment.rate.model.ExchangeRateRepository
import com.fx.exchange_experiment.rate.model.data.Currency
import com.fx.exchange_experiment.rate.model.data.ExchangeRateResponse
import com.fx.exchange_experiment.rate.model.worker.PeriodicExchangeRateWorker
import com.fx.exchange_experiment.rate.viewmodels.ExchangeRateViewModel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf.*
import org.hamcrest.core.Is.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ExchangeRateViewModelTest {

    private lateinit var db: CurrencyExchangeDatabase
    private lateinit var repository: ExchangeRateRepository
    private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @MockK
    private lateinit var currencyLayerService: CurrencyLayerService

    private lateinit var exchangeViewModel: ExchangeRateViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, CurrencyExchangeDatabase::class.java).build()
        val exchangeDao = db.getExchangeRateDao()

        repository = ExchangeRateRepository(
            exchangeDao,
            currencyLayerService,
            CurrencyExchangeConverter()
        )
        exchangeViewModel = ExchangeRateViewModel(repository)
        initializeWorkManager(context)
    }

    private fun initializeWorkManager(context: Context) {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(ExchangeRateWorkerFactory(repository))
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @ExperimentalTime
    @Test
    fun test_that_we_can_get_rates_successfully() = runBlocking {
        //Arrange
        val fakeRates = Faker.fakeRates()
        coEvery { currencyLayerService.getRates() } coAnswers {
            flow { emit(NetworkResponse.Success(ExchangeRateResponse(quotes = fakeRates))) }
        }

        //Act
        repository.getCurrencyRates().drop(2).test {
            val successResponse = awaitItem()
            //Assert
            assertThat(successResponse, instanceOf(Resource.Success::class.java))
            if(successResponse is Resource.Success) {
                assertThat(successResponse.data.size, `is`(fakeRates.size))
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_that_we_can_periodically_fetch_rates() = runBlocking {
        //Arrange
        val fakeRates = Faker.fakeRates()
        coEvery { currencyLayerService.getRates() } coAnswers {
            flow { emit(NetworkResponse.Success(ExchangeRateResponse(quotes = fakeRates))) }
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        val periodExchangeRequest = PeriodicWorkRequestBuilder<PeriodicExchangeRateWorker>(
            30, TimeUnit.MINUTES
        ).build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)

        //Act
        workManager.enqueue(periodExchangeRequest).result.get()

        testDriver?.setPeriodDelayMet(periodExchangeRequest.id)

        val workInfo = workManager.getWorkInfoById(periodExchangeRequest.id).get()

        //Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @ExperimentalTime
    @Test
    fun test_that_we_can_get_rate_for_specific_currencies() = runBlocking{
        //Arrange
        val fakeRates = Faker.fakeRates()
        coEvery { currencyLayerService.getRates() } coAnswers {
            flow { emit(NetworkResponse.Success(ExchangeRateResponse(quotes = fakeRates))) }
        }

        repository.getCurrencyRates().drop(2).first()

        val currency = Currency.NGN

        //Act & Assert
        repository.getExchangeRatesForCurrency(base = currency).drop(1).test {
            val response = awaitItem()

            assertThat(
                response,
                instanceOf(Resource.Success::class.java)
            )

            if(response is Resource.Success) {
                assertThat(response.data.size, `is`(fakeRates.size))
                //check that NGN is 1.0
                val ngnToNng = response.data.find {
                    it.baseCurrency.code == currency.code
                            && it.quoteCurrency.code == currency.code
                }
                assertThat(ngnToNng!!.rate, IsEqual.equalTo(1.0))
            }
            cancelAndIgnoreRemainingEvents()
        }
    }


    @After
    @Throws(IOException::class)
    fun tearDown() {
        unmockkAll()
        db.close()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    /**
     * A Worker factory for producing our worker for test
     */
   class ExchangeRateWorkerFactory(private val repository: ExchangeRateRepository) : WorkerFactory(){
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return when(workerClassName) {
                PeriodicExchangeRateWorker::class.java.name -> {
                    PeriodicExchangeRateWorker(
                        appContext,
                        workerParameters,
                        repository
                    )
                }
                else -> null
            }
        }
    }

}