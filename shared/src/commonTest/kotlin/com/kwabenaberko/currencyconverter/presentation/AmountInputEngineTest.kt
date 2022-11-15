package com.kwabenaberko.currencyconverter.presentation

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.presentation.AmountInputEngine.Amount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AmountInputEngineTest {

    private val sut = RealAmountInputEngine()

    @Test
    fun `should ignore non digit characters`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('A')
            expectNoEvents()
        }
    }

    @Test
    fun `should append digit to amount`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('2')
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.append('.')
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.append('0')
            assertEquals(Amount(text = "2.0", isValid = true), awaitItem())
        }
    }

    @Test
    fun `should remove last digit entered`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('2')
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.undo()
            assertEquals(Amount(text = "", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore undo when amount is already empty`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('2')
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.undo()
            assertEquals(Amount(text = "", isValid = false), awaitItem())

            sut.undo()
            expectNoEvents()
        }
    }

    @Test
    fun `should prepend 0 if initial value appended is dot`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('.')
            assertEquals(Amount(text = "0.", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore dot append when amount already contains a dot symbol`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('2')
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.append('.')
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.append('.')
            expectNoEvents()
        }
    }

    @Test
    fun `should ignore append of 0 when trailing character after dot is already zero`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.append('2')
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.append('.')
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.append('0')
            assertEquals(Amount(text = "2.0", isValid = true), awaitItem())

            sut.append('0')
            expectNoEvents()
        }
    }
}
