package com.kwabenaberko.converter.presentation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AmountInputEngineTest {

    private val sut = AmountInputEngine()

    @Test
    fun `should ignore non digit characters`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("A")
            expectNoEvents()
        }
    }

    @Test
    fun `should append digit to amount`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("2")
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.add(".")
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.add("0")
            assertEquals(Amount(text = "2.0", isValid = true), awaitItem())
        }
    }

    @Test
    fun `should remove last digit entered`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("2")
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.pop()
            assertEquals(Amount(text = "", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore undo when amount is already empty`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("2")
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.pop()
            assertEquals(Amount(text = "", isValid = false), awaitItem())

            sut.pop()
            expectNoEvents()
        }
    }

    @Test
    fun `should prepend 0 if initial value appended is dot`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add(".")
            assertEquals(Amount(text = "0.", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore dot append when amount already contains a dot symbol`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("2")
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.add(".")
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.add(".")
            expectNoEvents()
        }
    }

    @Test
    fun `should ignore append of 0 when trailing character after dot is already zero`() = runTest {
        sut.amount.test {
            assertEquals(Amount(), awaitItem())

            sut.add("2")
            assertEquals(Amount(text = "2", isValid = true), awaitItem())

            sut.add(".")
            assertEquals(Amount(text = "2.", isValid = false), awaitItem())

            sut.add("0")
            assertEquals(Amount(text = "2.0", isValid = true), awaitItem())

            sut.add("0")
            expectNoEvents()
        }
    }
}
