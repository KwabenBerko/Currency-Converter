package com.kwabenaberko.currencyconverter.android.sync

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.android.MainDispatcherRule
import com.kwabenaberko.currencyconverter.android.sync.SyncViewModel.State
import com.kwabenaberko.sharedtest.testdouble.FakeHasCompletedInitialSync
import com.kwabenaberko.sharedtest.testdouble.FakeSync
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SyncViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val hasCompletedInitialSync = FakeHasCompletedInitialSync().apply {
        this.result = false
    }
    private val sync = FakeSync().apply {
        this.result = false
    }

    @Test
    fun `should emit SyncError state when sync is not successful`() = runTest {
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(State.Syncing, awaitItem())
            assertEquals(State.SyncError, awaitItem())
        }
    }

    @Test
    fun `should emit SyncSuccess state when sync is successful`() = runTest {
        sync.result = true
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(State.Syncing, awaitItem())
            assertEquals(State.SyncSuccess, awaitItem())
        }
    }

    @Test
    fun `should emit SyncSuccess state if has already completed initial sync`() = runTest {
        hasCompletedInitialSync.result = true
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(State.SyncSuccess, awaitItem())
        }
    }

    private fun createViewModel(): SyncViewModel {
        return SyncViewModel(
            hasCompletedInitialSync = hasCompletedInitialSync,
            sync = sync
        )
    }
}
