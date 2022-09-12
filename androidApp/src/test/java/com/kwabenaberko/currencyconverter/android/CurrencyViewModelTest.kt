package com.kwabenaberko.currencyconverter.android

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.android.CurrencyViewModel.State
import com.kwabenaberko.currencyconverter.domain.model.CurrencyFilter
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.sharedtest.testdouble.FakeGetCurrencies
import com.kwabenaberko.sharedtest.testdouble.FakeGetSyncStatus
import com.kwabenaberko.sharedtest.testdouble.FakeHasCompletedInitialSync
import com.kwabenaberko.sharedtest.testdouble.FakeSync
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    private val sync = FakeSync()
    private val hasCompletedInitialSync = FakeHasCompletedInitialSync()
    private val getSyncStatus = FakeGetSyncStatus()
    private val getCurrencies = FakeGetCurrencies()
    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = FakeDispatcherProvider(dispatcher)

    @Test
    fun `should not initiate sync if initial sync has already been completed`() = runTest {
        hasCompletedInitialSync.result = true
        createCurrencyViewModel()

        assertEquals(0, sync.invocations)
    }

    @Test
    fun `should initiate sync if initial sync has not been completed`() = runTest {
        hasCompletedInitialSync.result = false
        createCurrencyViewModel()

        assertEquals(1, sync.invocations)
    }

    @Test
    fun `should track sync status changes and update state`() = runTest {
        val sut = createCurrencyViewModel()

        sut.state.test {
            assertEquals(State(), awaitItem())

            getSyncStatus.result.emit(SyncStatus.Error)
            assertEquals(State(syncStatus = SyncStatus.Error), awaitItem())

            getSyncStatus.result.emit(SyncStatus.InProgress)
            assertEquals(State(syncStatus = SyncStatus.InProgress), awaitItem())

            getSyncStatus.result.emit(SyncStatus.Success)
            assertEquals(State(syncStatus = SyncStatus.Success), awaitItem())
        }
    }

    @Test
    fun `should track currency changes and update state`() = runTest(dispatcher) {
        val expectedCurrencies = persistentMapOf(
            'G' to listOf(GHS),
            'N' to listOf(NGN),
            'U' to listOf(USD)
        )
        val sut = createCurrencyViewModel()

        sut.state.test {
            assertEquals(State(), awaitItem())

            advanceUntilIdle()

            getCurrencies.result.emit(listOf(GHS, NGN, USD))
            assertEquals(State(currencies = expectedCurrencies), awaitItem())
        }
    }

    @Test
    fun `should initiate get currencies by filter when searchQuery changes`() =
        runTest(dispatcher) {
            val query = "a query"
            val sut = createCurrencyViewModel()

            sut.state.test {
                assertEquals(State(), awaitItem())

                sut.search(query = query)
                assertEquals(State(searchQuery = query), awaitItem())

                advanceUntilIdle()

                assertEquals(1, getCurrencies.invocations.size)
                assertEquals(CurrencyFilter(name = query), getCurrencies.invocations.last())
            }
        }

    private fun createCurrencyViewModel(): CurrencyViewModel {
        return CurrencyViewModel(
            hasCompletedInitialSync = hasCompletedInitialSync,
            getSyncStatus = getSyncStatus,
            sync = sync,
            getCurrencies = getCurrencies,
            dispatcherProvider = dispatcherProvider
        )
    }

    companion object {
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val USD = makeDollarCurrency()
    }
}
