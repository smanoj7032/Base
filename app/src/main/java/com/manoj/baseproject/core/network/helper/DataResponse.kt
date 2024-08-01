package com.manoj.baseproject.core.network.helper

import com.google.gson.annotations.SerializedName

open class DataResponse<T> : BaseApiResponse() {
    @SerializedName("data")
    val data: T? = null
    override fun toString(): String {
        return "DataResponse{" +
                "data=" + data +
                '}'
    }
}