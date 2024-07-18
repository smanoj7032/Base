package com.manoj.baseproject.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.utils.helper.Resource
import com.manoj.baseproject.utils.helper.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Response

fun <T> Flow<Resource<T>>.emitter(
    mutableStateFlow: MutableStateFlow<Resource<T?>>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    this.onEach { state ->
        when (state.status) {
            Status.SUCCESS -> mutableStateFlow.value = Resource.success(state.data)
            Status.ERROR -> mutableStateFlow.value = Resource.error(null, state.message)
            else -> mutableStateFlow.value = Resource.loading()
        }
    }.catch { throwable ->
        val networkError = parseException(throwable)
        mutableStateFlow.value = Resource.error(null, networkError)
    }.launchIn(scope)
}


suspend fun <T> StateFlow<Resource<T>>.customCollector(

    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T?) -> Unit)?,
    onError: ((throwable: Throwable, isShow: Boolean) -> Unit)?,
) {
    collect { state ->
        when (state.status) {
            Status.LOADING -> {
                onLoading.invoke(true)
            }

            Status.SUCCESS -> {
                onSuccess?.invoke(state.data)
                onLoading.invoke(false)
            }

            Status.ERROR -> {
                onLoading.invoke(false)
                onError?.invoke(Throwable(state.message), true)
            }

            Status.WARN -> {

            }
        }
    }
}

suspend fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<Resource<T?>> {
    return flow {
        emit(Resource.loading<T>())
        try {
            if (MyApplication.instance.isOnline()) {
                val response = apiCall.invoke()
                Log.e("Response-->>", "executeApiCall: ${Gson().toJson(response.message())}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        emit(Resource.success(body))
                    } else {
                        emit(Resource.error<T>(null, "Response body is null"))
                    }
                } else {
                    val errorMessage = response.extractErrorMessage()
                    emit(Resource.error<T>(null, errorMessage))
                }
            } else {
                emit(Resource.error<T>(null, "Check your internet connection"))
            }
        } catch (e: Exception) {
            val exceptionMessage = parseException(e)
            emit(Resource.error<T>(null, exceptionMessage))
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