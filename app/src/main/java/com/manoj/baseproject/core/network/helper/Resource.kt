package com.manoj.baseproject.core.network.helper

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
</T> */
class Resource<T> private constructor(val status: Status, val data: T?, val message: String? = "") {

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", slot_list=" + data +
                '}'
    }

    companion object {
        fun <T> success(data: T?, msg: String = "Success"): Resource<T?> {
            return Resource(Status.SUCCESS, data, msg)
        }

        fun <T> warn(data: T? = null, msg: String? = "Warning"): Resource<T?> {
            return Resource(Status.WARN, data, msg)
        }

        fun <T> error(data: T? = null, errMsg: String? ): Resource<T?> {
            return Resource(Status.ERROR, data, errMsg)
        }


        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data)
        }

        fun <T> loading(): Resource<T?> {
            return Resource(Status.LOADING, null)
        }
    }

}