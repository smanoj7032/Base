package com.manoj.base.core.utils.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {
    fun getIO(): CoroutineDispatcher
    fun getMain(): CoroutineDispatcher
    fun getMainImmediate(): CoroutineDispatcher
    fun getDefault(): CoroutineDispatcher
}