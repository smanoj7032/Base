package com.manoj.baseproject.core.utils.extension

import com.manoj.baseproject.core.network.helper.apihelper.ApiStatus
import com.manoj.baseproject.core.network.helper.apihelper.BaseApiResponse
import com.manoj.baseproject.core.network.helper.apihelper.DataResponse
import com.manoj.baseproject.core.network.helper.apihelper.Result
import com.manoj.baseproject.core.network.helper.SystemVariables.isInternetConnected
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

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
        this@apiEmitter.onStart {
            stateFlow.emit(Result.Loading)
        }.catch { throwable ->
            stateFlow.emit(Result.Error(parseException(throwable)))
        }.collect { response ->
            stateFlow.emit(
                when (response.apiStatus) {
                    is ApiStatus.Success -> Result.Success(response.data)
                    is ApiStatus.Created -> Result.Success(response.data)
                    is ApiStatus.Accepted -> Result.Success(response.data)
                    is ApiStatus.NoContent -> Result.Success(null)
                    is ApiStatus.BadRequest -> Result.Error(response.message.toString())
                    is ApiStatus.Unauthorized -> Result.Error(response.message.toString())
                    is ApiStatus.Forbidden -> Result.Error(response.message.toString())
                    is ApiStatus.NotFound -> Result.Error(response.message.toString())
                    is ApiStatus.ServerError -> Result.Error(response.message.toString())
                    is ApiStatus.Unknown -> Result.Error("Unknown error occurred")
                }
            )
        }
    }
}

fun <M> Flow<BaseApiResponse>.simpleApiEmitter(
    stateFlow: MutableStateFlow<Result<M?>>,
    coroutineScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    coroutineScope.launch(dispatchersProvider.getIO()) {
        this@simpleApiEmitter.onStart {
            stateFlow.emit(Result.Loading)
        }.catch { throwable ->
            stateFlow.emit(Result.Error(parseException(throwable)))
        }.collect { response ->
            stateFlow.emit(
                when (response.apiStatus) {
                    is ApiStatus.Success -> Result.Success()
                    is ApiStatus.Created -> Result.Success()
                    is ApiStatus.Accepted -> Result.Success()
                    is ApiStatus.NoContent -> Result.Success()
                    is ApiStatus.BadRequest -> Result.Error(response.message.toString())
                    is ApiStatus.Unauthorized -> Result.Error(response.message.toString())
                    is ApiStatus.Forbidden -> Result.Error(response.message.toString())
                    is ApiStatus.NotFound -> Result.Error(response.message.toString())
                    is ApiStatus.ServerError -> Result.Error(response.message.toString())
                    is ApiStatus.Unknown -> Result.Error("Unknown error occurred")
                }
            )
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
            this@defaultEmitter
                .onStart { stateFlow.emit(Result.Loading) }
                .catch { stateFlow.emit(Result.Error(parseException(it))) }
                .collect { stateFlow.emit(Result.Success(it)) }
        } else stateFlow.emit(Result.Error("No internet connection"))
    }
}


suspend fun <T> StateFlow<Result<T>>.customCollector(
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T?) -> Unit),
    onError: ((throwable: String?, isShow: Boolean) -> Unit),
) {
    collect { state ->
        when (state) {
            is Result.Success -> {
                onSuccess.invoke(state.data)
                onLoading.invoke(false)
            }

            is Result.Error -> {
                onLoading.invoke(false)
                onError.invoke(state.message, true)
            }

            is Result.Loading -> {
                onLoading.invoke(true)
            }
        }
    }
}