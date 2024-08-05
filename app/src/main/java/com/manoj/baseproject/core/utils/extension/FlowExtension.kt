package com.manoj.baseproject.core.utils.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.manoj.baseproject.core.network.helper.DataResponse
import com.manoj.baseproject.core.network.helper.Result
import com.manoj.baseproject.core.network.helper.SystemVariables.isInternetConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import retrofit2.Response

fun <T> Flow<Result<T>>.emitter(
    mutableStateFlow: MutableStateFlow<Result<T?>>, scope: CoroutineScope
) {
    this.onEach { state ->
        when (state) {
            is Result.Success -> mutableStateFlow.emit(Result.Success(state.data))
            is Result.Error -> mutableStateFlow.emit(Result.Error(state.message))
            else -> mutableStateFlow.emit(Result.Loading)
        }
    }.catch { throwable ->
        val networkError = parseException(throwable)
        mutableStateFlow.value = Result.Error(networkError)
    }.onStart { mutableStateFlow.emit(Result.Loading) }.launchIn(scope)
}

suspend fun <T> StateFlow<Result<T>>.customCollector(
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T?) -> Unit)?,
    onError: ((throwable: String?, isShow: Boolean) -> Unit)?,
) {
    collect { state ->
        when (state) {
            is Result.Success -> {
                onSuccess?.invoke(state.data)
                onLoading.invoke(false)
            }

            is Result.Error -> {
                onLoading.invoke(false)
                onError?.invoke(state.message, true)
            }

            is Result.Loading -> {
                onLoading.invoke(true)
            }
        }
    }
}

suspend fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<Result<T?>> {
    return flow {
        if (isInternetConnected) {
            val response = apiCall.invoke()
            Log.e("Response-->>", "executeApiCall: ${Gson().toJson(response.message())}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(Result.Success(body))
                } else {
                    emit(Result.Error("Response body is null"))
                }
            } else {
                val errorMessage = response.extractErrorMessage()
                emit(Result.Error(errorMessage))
            }
        } else {
            emit(Result.Error("No internet connection"))
        }

    }.catch {
        emit(Result.Error(parseException(it)))
    }.onStart { emit(Result.Loading) }.flowOn(Dispatchers.IO)
}

suspend fun <T> executeNetworkCall(apiCall: suspend () -> DataResponse<T>): Flow<Result<T?>> {
    return flow {
        if (isInternetConnected) {
            val response = apiCall.invoke()
            Log.e("Response-->>", "executeApiCall: ${Gson().toJson(response.message)}")
            if (response.isStatusOK) {
                val body = response.data
                if (body != null) {
                    this.emit(Result.Success(body))
                } else {
                    this.emit(Result.Error("Response body is null"))
                }
            } else {
                val errorMessage = response.message ?: ""
                this.emit(Result.Error(errorMessage))
            }
        } else {
            this.emit(Result.Error("No internet connection"))
        }

    }.catch {
        emit(Result.Error(parseException(it)))
    }.onStart { emit(Result.Loading) }.flowOn(Dispatchers.IO)
}

fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            return true
        }
    }
    return false
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this.map<T, Result<T>> { Result.Success(it) }.onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(parseException(it))) }
}