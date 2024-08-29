package com.manoj.base.core.network.helper.apihelper

import com.google.gson.annotations.SerializedName
import com.manoj.base.core.utils.extension.toJson

data class DataResponse<T>(
    @SerializedName("data")
    val data: T? = null
) : BaseApiResponse() {

    override fun toString(): String {
        return "DataResponse(data=${data.toJson()}) ${super.toString()}"
    }
}