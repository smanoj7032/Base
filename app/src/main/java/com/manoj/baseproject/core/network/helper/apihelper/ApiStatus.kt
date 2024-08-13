package com.manoj.baseproject.core.network.helper.apihelper

import java.net.HttpURLConnection.*

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
    HTTP_OK -> ApiStatus.Success
    HTTP_CREATED -> ApiStatus.Created
    HTTP_ACCEPTED -> ApiStatus.Accepted
    HTTP_NO_CONTENT -> ApiStatus.NoContent
    HTTP_BAD_REQUEST -> ApiStatus.BadRequest
    HTTP_UNAUTHORIZED -> ApiStatus.Unauthorized
    HTTP_FORBIDDEN -> ApiStatus.Forbidden
    HTTP_NOT_FOUND -> ApiStatus.NotFound
    in 500..599 -> ApiStatus.ServerError
    else -> ApiStatus.Unknown
}
