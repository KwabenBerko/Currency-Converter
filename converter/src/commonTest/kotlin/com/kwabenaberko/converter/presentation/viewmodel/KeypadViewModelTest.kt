package com.kwabenaberko.converter.presentation.viewmodel

import app.cash.turbine.test
import com.kwabenaberko.converter.presentation.Amount
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


@OptIn(ExperimentalCoroutinesApi::class)
class KeypadViewModelTest {
    private lateinit var sut: KeypadViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        sut = KeypadViewModel()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit Amount state when a character is added`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add('1')
            assertEquals(Amount(text = "1", isValid = true), awaitItem())
        }
    }

    @Test
    fun `should emit Amount state when a pop operation occurs`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add('1')
            assertEquals(Amount(text = "1", isValid = true), awaitItem())

            sut.pop()
            assertEquals(Amount(), awaitItem())
        }
    }
}
