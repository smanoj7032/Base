package com.manoj.baseproject.network.helper

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.HttpURLConnection

open class BaseApiResponse : Serializable {
    @SerializedName("code")
    var status :Int?= null

    @SerializedName("message")
    var message: String? = null
        protected set

    @SerializedName("method")
    var method: String? = null
        protected set

    override fun toString(): String {
        return "SimpleApiResponse{" +
                "success=" + status +
                ", message='" + message + '\'' +
                '}'
    }

    val isStatusOK: Boolean
        get() = status == HttpURLConnection.HTTP_OK
}