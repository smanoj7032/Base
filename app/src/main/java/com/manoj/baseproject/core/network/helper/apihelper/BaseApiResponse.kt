package com.manoj.baseproject.core.network.helper.apihelper

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
}