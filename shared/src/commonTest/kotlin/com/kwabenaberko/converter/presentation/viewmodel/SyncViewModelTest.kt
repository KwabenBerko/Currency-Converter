package com.kwabenaberko.converter.presentation.viewmodel

import app.cash.turbine.test
import com.kwabenaberko.converter.presentation.viewmodel.SyncViewModel.State
import com.kwabenaberko.converter.testdouble.FakeSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class SyncViewModelTest {
    private val sync = FakeSync().apply {
        this.result = false
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
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
