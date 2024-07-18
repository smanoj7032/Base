package com.manoj.baseproject.utils

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.utils.helper.Resource
import com.manoj.baseproject.utils.helper.SingleRequestEvent
import com.manoj.baseproject.utils.helper.Status
import com.manoj.baseproject.network.helper.DataResponse
import com.manoj.baseproject.network.helper.BaseApiResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection


fun <M> Single<DataResponse<M>>.apiSubscription(liveData: SingleRequestEvent<M>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<M>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(it.data, it.message.toString()))
        else liveData.postValue(Resource.warn(null, it.message.toString()))
    }, { it ->
        val error = parseException(it)
        error.let {
            liveData.postValue(Resource.error(null, it))
        }
    })
}


fun Single<BaseApiResponse>.simpleSubscription(liveData: SingleRequestEvent<Void>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<Void>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(null, it.message.toString()))
        else liveData.postValue(Resource.warn(null, it.message.toString()))
    }, { it ->
        val error = parseException(it);
        error.let {
            liveData.postValue(Resource.error(null, it))
        }

    })
}

fun <B> Single<B>.customSubscription(liveData: SingleRequestEvent<B>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<B>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        liveData.postValue(Resource.success(it, "Successful"))
    }, { it ->
        val error = parseException(it);
        error.let {
            liveData.postValue(Resource.error(null, it))
        }
    })
}

fun <M> Single<BaseApiResponse>.simpleSubscriptionWithTag(
    tag: M, liveData: SingleRequestEvent<M>
): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<M>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(tag, it.message.toString()))
        else liveData.postValue(Resource.warn(tag, it.message.toString()))
    }, { it ->
        val error = parseException(it)
        error.let {
            liveData.postValue(Resource.error(tag, it))
        }

    })
}

fun <T> SingleRequestEvent<T>.singleObserver(
    owner: LifecycleOwner,
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T) -> Unit),
    onError: ((throwable: Throwable, showError: Boolean) -> Unit),
) {
    this.observe(owner, SingleRequestEvent.RequestObserver { result ->

        when (result.status) {
            Status.LOADING -> onLoading(true)
            Status.SUCCESS -> {
                onLoading(false)
                result.data?.let { onSuccess(it) }
            }

            else -> {
                onLoading(false)
                onError(Throwable(result.message), true)
            }
        }
    })
}

/**Extension function to extract error message from Response*/
 fun <T> Response<T>.extractErrorMessage(): String {
    return try {
        val errorBodyString = errorBody()?.string()
        if (!errorBodyString.isNullOrEmpty()) {
            JSONObject(errorBodyString).getString("message")
        } else {
            message()
        }
    } catch (e: Exception) {
        message()
    }
}

/**Function to parse exceptions and return appropriate messages*/
fun parseException(throwable: Throwable?): String {
    Log.e("ApiCall", "Exception: ${throwable?.message}", throwable)

    return when (throwable) {
        is HttpException -> {
            when (throwable.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    val message = throwable.extractErrorText()
                    if (message.contains("Unauthorized", true)) {
                        MyApplication.instance.onLogout()
                        "Session expired. Please log in again."
                    } else {
                        message
                    }
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> "Internal server error. Please try again later."
                HttpURLConnection.HTTP_BAD_REQUEST -> "Bad request. Please check your input."
                HttpURLConnection.HTTP_FORBIDDEN -> "You don't have permission to access this resource."
                HttpURLConnection.HTTP_NOT_FOUND -> "Requested resource not found."
                HttpURLConnection.HTTP_BAD_GATEWAY -> "Bad gateway. Please try again later."
                else -> throwable.extractErrorText()
            }
        }
        is IOException -> "Slow or No Internet Access"
        else -> throwable?.message ?: "An unexpected error occurred"
    }
}

/**Extension function to extract error text from HttpException*/
 fun HttpException.extractErrorText(): String {
    return try {
        val errorBodyString = response()?.errorBody()?.string()
        if (!errorBodyString.isNullOrEmpty()) {
            JSONObject(errorBodyString).getString("message")
        } else {
            message()
        }
    } catch (e: Exception) {
        message()
    }
}

class UnAuthUser
