package com.manoj.baseproject.core.network.helper

sealed interface ApiStatus {
    data object Success : ApiStatus
    data object Created : ApiStatus
    data object Accepted : ApiStatus
    data object NoContent : ApiStatus
    data object BadRequest : ApiStatus
    data object Unauthorized : ApiStatus
    data object Forbidden : ApiStatus
    data object NotFound : ApiStatus
    data object ServerError : ApiStatus
    data object Unknown : ApiStatus
}

fun getApiStatusFromCode(code: Int?) = when (code) {
    in 200..299 -> ApiStatus.Success
    201 -> ApiStatus.Created
    202 -> ApiStatus.Accepted
    204 -> ApiStatus.NoContent
    400 -> ApiStatus.BadRequest
    401 -> ApiStatus.Unauthorized
    403 -> ApiStatus.Forbidden
    404 -> ApiStatus.NotFound
    in 500..599 -> ApiStatus.ServerError
    else -> ApiStatus.Unknown
}
