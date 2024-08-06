package com.manoj.baseproject.core.network.helper

import com.google.gson.annotations.SerializedName

data class DataResponse<T>(
    @SerializedName("data")
    val data: T? = null
) : BaseApiResponse() {

    override fun toString(): String {
        return "DataResponse(data=$data) ${super.toString()}"
    }
}