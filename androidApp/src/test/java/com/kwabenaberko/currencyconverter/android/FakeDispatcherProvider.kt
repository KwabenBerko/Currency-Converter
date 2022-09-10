package com.kwabenaberko.currencyconverter.android

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class FakeDispatcherProvider constructor(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = dispatcher

    override val io: CoroutineDispatcher
        get() = dispatcher
}
