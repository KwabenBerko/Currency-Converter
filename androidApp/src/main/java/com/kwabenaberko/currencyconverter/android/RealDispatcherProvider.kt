package com.kwabenaberko.currencyconverter.android

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class RealDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}
