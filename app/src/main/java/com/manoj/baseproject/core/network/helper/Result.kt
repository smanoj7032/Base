package com.manoj.baseproject.core.network.helper

sealed interface Result<out T> {
    data class Success<T>(val data: T, val message: String? = null) : Result<T>
    data class Error(val message: String) : Result<Nothing>
    data object Loading : Result<Nothing>
}