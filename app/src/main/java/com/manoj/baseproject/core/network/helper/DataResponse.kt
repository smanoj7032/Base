package com.manoj.baseproject.core.network.helper

import com.google.gson.annotations.SerializedName
import com.manoj.baseproject.core.utils.extension.toJson

data class DataResponse<T>(
    @SerializedName("data")
    val data: T? = null
) : BaseApiResponse() {

    override fun toString(): String {
        return "DataResponse(data=${data.toJson()}) ${super.toString()}"
    }
}