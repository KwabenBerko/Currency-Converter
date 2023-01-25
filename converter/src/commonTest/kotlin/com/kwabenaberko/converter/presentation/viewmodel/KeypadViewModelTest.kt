package com.kwabenaberko.converter.presentation.viewmodel

import app.cash.turbine.test
import com.kwabenaberko.converter.presentation.viewmodel.KeypadViewModel.State
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
    fun `should ignore non digit characters`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("A")
            expectNoEvents()
        }
    }

    @Test
    fun `should append digit to amount`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("2")
            assertEquals(State(text = "2", isValid = true), awaitItem())

            sut.append(".")
            assertEquals(State(text = "2.", isValid = false), awaitItem())

            sut.append("0")
            assertEquals(State(text = "2.0", isValid = true), awaitItem())
        }
    }

    @Test
    fun `should remove last digit entered`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("2")
            assertEquals(State(text = "2", isValid = true), awaitItem())

            sut.removeLast()
            assertEquals(State(text = "", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore remove action when amount is already empty`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("2")
            assertEquals(State(text = "2", isValid = true), awaitItem())

            sut.removeLast()
            assertEquals(State(text = "", isValid = false), awaitItem())

            sut.removeLast()
            expectNoEvents()
        }
    }

    @Test
    fun `should prepend 0 if initial value appended is dot`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append(".")
            assertEquals(State(text = "0.", isValid = false), awaitItem())
        }
    }

    @Test
    fun `should ignore dot append when amount already contains a dot symbol`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("2")
            assertEquals(State(text = "2", isValid = true), awaitItem())

            sut.append(".")
            assertEquals(State(text = "2.", isValid = false), awaitItem())

            sut.append(".")
            expectNoEvents()
        }
    }

    @Test
    fun `should ignore append of 0 when trailing character after dot is already zero`() = runTest {
        sut.state.test {
            assertEquals(State(), awaitItem())

            sut.append("2")
            assertEquals(State(text = "2", isValid = true), awaitItem())

            sut.append(".")
            assertEquals(State(text = "2.", isValid = false), awaitItem())

            sut.append("0")
            assertEquals(State(text = "2.0", isValid = true), awaitItem())

            sut.append("0")
            expectNoEvents()
        }
    }
}
