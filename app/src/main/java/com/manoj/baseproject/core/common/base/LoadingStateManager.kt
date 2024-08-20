package com.manoj.baseproject.core.common.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingStateManager @Inject constructor() {
    private val _loadingState = MutableStateFlow(false)

    val loadingState: StateFlow<Boolean> get() = _loadingState

    fun showLoading() {
        _loadingState.value = true
    }

    fun hideLoading() {
        _loadingState.value = false
    }
}