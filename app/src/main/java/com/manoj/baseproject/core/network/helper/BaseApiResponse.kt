package com.manoj.baseproject.core.network.helper

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseApiResponse : Serializable {
    @SerializedName("code")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null
        protected set

    @SerializedName("method")
    var method: String? = null
        protected set

    override fun toString(): String {
        return "BaseApiResponse{" +
                "success=" + status +
                ", message='" + message + '\'' +
                '}'
    }

    val apiStatus: ApiStatus
        get() = getApiStatusFromCode(status)
    val isStatusOK: Boolean get() = status in 200..299
    val isStatusCreated: Boolean get() = status == 201
    val isStatusAccepted: Boolean get() = status == 202
    val isStatusNoContent: Boolean get() = status == 204
    val isStatusBadRequest: Boolean get() = status == 400
    val isStatusUnauthorized: Boolean get() = status == 401
    val isStatusForbidden: Boolean get() = status == 403
    val isStatusNotFound: Boolean get() = status == 404
    val isStatusServerError: Boolean get() = status in 500..599
}