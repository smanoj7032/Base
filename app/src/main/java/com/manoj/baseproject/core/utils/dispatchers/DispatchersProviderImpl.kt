package com.manoj.baseproject.core.utils.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DispatchersProviderImpl @Inject constructor() : DispatchersProvider {
    override fun getMain(): CoroutineDispatcher = Dispatchers.Main
    override fun getMainImmediate(): CoroutineDispatcher = Dispatchers.Main.immediate
    override fun getIO(): CoroutineDispatcher = Dispatchers.IO
    override fun getDefault(): CoroutineDispatcher = Dispatchers.Default
}