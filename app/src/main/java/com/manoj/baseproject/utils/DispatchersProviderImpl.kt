package com.manoj.baseproject.utils

import com.manoj.baseproject.utils.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatchersProviderImpl : DispatchersProvider {
    override fun getMain(): CoroutineDispatcher = Dispatchers.Main
    override fun getMainImmediate(): CoroutineDispatcher = Dispatchers.Main.immediate
    override fun getIO(): CoroutineDispatcher = Dispatchers.IO
    override fun getDefault(): CoroutineDispatcher = Dispatchers.Default
}