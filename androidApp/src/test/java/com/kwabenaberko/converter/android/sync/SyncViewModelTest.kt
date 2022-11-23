package com.kwabenaberko.converter.android.sync

import app.cash.turbine.test
import com.kwabenaberko.converter.android.MainDispatcherRule
import com.kwabenaberko.converter.android.sync.SyncViewModel.State
import com.kwabenaberko.convertertest.testdouble.FakeSync
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SyncViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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

    private fun createViewModel(): SyncViewModel {
        return SyncViewModel(sync)
    }
}
