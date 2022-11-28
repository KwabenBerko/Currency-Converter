package com.kwabenaberko.converter.android.keypad

import app.cash.turbine.test
import com.kwabenaberko.converter.presentation.Amount
import com.kwabenaberko.currencyconverter.android.keypad.KeypadViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeypadViewModelTest {
    private val sut = KeypadViewModel()

    @Test
    fun `should emit Amount state when a character is added`() = runTest {
        sut.state.test {
            assertEquals(Amount(), awaitItem())

            sut.append('1')
            assertEquals(Amount(text = "1", isValid = true), awaitItem())
        }
    }

    @Test
    fun `should emit Amount state when a pop operation occurs`() = runTest {
        sut.state.test {
            assertEquals(Amount(), awaitItem())

            sut.append('1')
            assertEquals(Amount(text = "1", isValid = true), awaitItem())

            sut.undo()
            assertEquals(Amount(), awaitItem())
        }
    }
}
