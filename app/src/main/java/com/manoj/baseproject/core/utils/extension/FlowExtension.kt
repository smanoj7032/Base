package com.manoj.baseproject.core.utils.extension

import android.util.Log
import com.google.gson.Gson
import com.manoj.baseproject.core.network.helper.ApiStatus
import com.manoj.baseproject.core.network.helper.BaseApiResponse
import com.manoj.baseproject.core.network.helper.DataResponse
import com.manoj.baseproject.core.network.helper.Result
import com.manoj.baseproject.core.network.helper.SystemVariables.isInternetConnected
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
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
import kotlinx.coroutines.launch
import retrofit2.Response

fun <T> Flow<Result<T>>.emitter(
    mutableStateFlow: MutableStateFlow<Result<T?>>, scope: CoroutineScope
) {
    this.onEach { state ->
        mutableStateFlow.emit(
            when (state) {
                is Result.Success -> Result.Success(state.data)
                is Result.Error -> Result.Error(state.message)
                else -> Result.Loading
            }
        )
    }.catch { throwable ->
        val networkError = parseException(throwable)
        mutableStateFlow.value = Result.Error(networkError)
    }.onStart { mutableStateFlow.emit(Result.Loading) }.launchIn(scope)
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

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this.map<T, Result<T>> { Result.Success(it) }.onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(parseException(it))) }
}

fun <M> Flow<DataResponse<M>>.apiEmitter(
    stateFlow: MutableStateFlow<Result<M?>>,
    coroutineScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    coroutineScope.launch(dispatchersProvider.getIO()) {
        this@apiEmitter
            .onStart {
                stateFlow.value = Result.Loading
            }
            .catch { throwable ->
                val error = parseException(throwable)
                stateFlow.value = Result.Error(error)
            }
            .collect { response ->
                when (response.apiStatus) {
                    is ApiStatus.Success -> stateFlow.value = Result.Success(response.data)
                    is ApiStatus.Created -> stateFlow.value = Result.Success(response.data)
                    is ApiStatus.Accepted -> stateFlow.value = Result.Success(response.data)
                    is ApiStatus.NoContent -> stateFlow.value = Result.Success(null)
                    is ApiStatus.BadRequest -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Unauthorized -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Forbidden -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.NotFound -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.ServerError -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Unknown -> stateFlow.value = Result.Error("Unknown error occurred")
                }
            }
    }
}

fun <M> Flow<BaseApiResponse>.simpleApiEmitter(
    data: M, stateFlow: MutableStateFlow<Result<M?>>, coroutineScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    coroutineScope.launch(dispatchersProvider.getIO()) {
        this@simpleApiEmitter
            .onStart {
                stateFlow.value = Result.Loading
            }
            .catch { throwable ->
                val error = parseException(throwable)
                stateFlow.value = Result.Error(error)
            }
            .collect { response ->
                when (response.apiStatus) {
                    is ApiStatus.Success -> stateFlow.value = Result.Success()
                    is ApiStatus.Created -> stateFlow.value = Result.Success()
                    is ApiStatus.Accepted -> stateFlow.value = Result.Success()
                    is ApiStatus.NoContent -> stateFlow.value = Result.Success()
                    is ApiStatus.BadRequest -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Unauthorized -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Forbidden -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.NotFound -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.ServerError -> stateFlow.value =
                        Result.Error(response.message.toString())

                    is ApiStatus.Unknown -> stateFlow.value = Result.Error("Unknown error occurred")
                }
            }
    }
}

fun <B> Flow<B>.defaultEmitter(
    stateFlow: MutableStateFlow<Result<B?>>,
    coroutineScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    coroutineScope.launch(dispatchersProvider.getIO()) {
        if (isInternetConnected) {
            this@defaultEmitter.onStart {
                stateFlow.emit(Result.Loading)
            }
                .catch { throwable ->
                    val error = parseException(throwable)
                    stateFlow.emit(Result.Error(error))
                }
                .collect { data ->
                    stateFlow.emit(Result.Success(data))
                }
        } else stateFlow.emit(Result.Error("No internet connection"))
    }
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