package com.manoj.baseproject.core.network.helper

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.HttpURLConnection

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

    val isStatusOK: Boolean get() = status in 200..299
}