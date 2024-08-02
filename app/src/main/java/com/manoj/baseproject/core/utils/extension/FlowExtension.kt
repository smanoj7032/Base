package com.manoj.baseproject.core.utils.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.manoj.baseproject.MyApplication.Companion.instance
import com.manoj.baseproject.core.network.helper.BaseApiResponse
import com.manoj.baseproject.core.network.helper.DataResponse
import com.manoj.baseproject.core.network.helper.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.Response

fun <T> Flow<Result<T>>.emitter(
    mutableStateFlow: MutableStateFlow<Result<T?>>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    this.onEach { state ->
        when (state) {
            is Result.Success -> mutableStateFlow.value = Result.Success(state.data)
            is Result.Error -> mutableStateFlow.value = Result.Error(state.message)
            else -> mutableStateFlow.value = Result.Loading
        }
    }.catch { throwable ->
        val networkError = parseException(throwable)
        mutableStateFlow.value = Result.Error(networkError)
    }.launchIn(scope)
}

fun <M> Flow<DataResponse<M>>.apiSubscription(
    stateFlow: MutableStateFlow<Result<M?>>,
    coroutineScope: CoroutineScope
): Job {
    return coroutineScope.launch {
        this@apiSubscription
            .onStart {
                stateFlow.value = Result.Loading
            }
            .flowOn(Dispatchers.IO)
            .catch { throwable ->
                val error = parseException(throwable)
                stateFlow.value = Result.Error(error)
            }
            .collect { response ->
                if (response.isStatusOK) {
                    stateFlow.value = Result.Success(response.data)
                } else {
                    stateFlow.value = Result.Error(response.message.toString())
                }
            }
    }
}

fun <M> Flow<BaseApiResponse>.simpleSubscriptionWithTag(
    tag: M, stateFlow: MutableStateFlow<Result<M?>>, coroutineScope: CoroutineScope
): Job {
    return coroutineScope.launch {
        this@simpleSubscriptionWithTag
            .onStart {
                stateFlow.value = Result.Loading
            }
            .flowOn(Dispatchers.IO)
            .catch { throwable ->
                val error = parseException(throwable)
                stateFlow.value = Result.Error(error)
            }
            .collect { response ->
                if (response.isStatusOK) {
                    stateFlow.value = Result.Success(tag)
                } else {
                    stateFlow.value = Result.Error(response.message.toString())
                }
            }
    }
}

fun <B> Flow<B>.customSubscription(
    stateFlow: MutableStateFlow<Result<B?>>,
    coroutineScope: CoroutineScope
): Job {
    return coroutineScope.launch {
        if (instance.isOnline()) {
            this@customSubscription
                .flowOn(Dispatchers.IO)
                .catch { throwable ->
                    val error = parseException(throwable)
                    stateFlow.value = Result.Error(error)
                }
                .collect { data ->
                    stateFlow.value = Result.Success(data)
                }
        } else stateFlow.value = Result.Error("No internet connection")
    }
}


suspend fun <T> StateFlow<Result<T>>.customCollector(
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T?) -> Unit)?,
    onError: ((throwable: String?, isShow: Boolean) -> Unit)?,
) {
    collect { state ->
        /*when (state.status) {
            Status.LOADING -> {
                onLoading.invoke(true)
            }

            Status.SUCCESS -> {
                onSuccess?.invoke(state.data)
                onLoading.invoke(false)
            }

            Status.ERROR -> {
                onLoading.invoke(false)
                onError?.invoke(state.message, true)
            }

            Status.WARN -> {
                onLoading.invoke(false)
                onError?.invoke(state.message, true)
            }
        }*/
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
        emit(Result.Loading)
        try {
            if (instance.isOnline()) {
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
        } catch (e: Exception) {
            val exceptionMessage = parseException(e)
            emit(Result.Error(exceptionMessage))
        }
    }.flowOn(Dispatchers.IO)
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