package com.manoj.base.core.utils.extension

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.manoj.base.MyApplication
import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.network.helper.SingleRequestEvent
import com.manoj.base.core.network.helper.apihelper.DataResponse
import com.manoj.base.core.network.helper.apihelper.BaseApiResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection


fun <M> Single<DataResponse<M>>.apiSubscription(liveData: SingleRequestEvent<M>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Result.Loading)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Result.Success(it.data))
        else liveData.postValue(Result.Error(it.message.toString()))
    }, { it ->
        val error = parseException(it)
        error.let {
            liveData.postValue(Result.Error(it))
        }
    })
}


fun Single<BaseApiResponse>.simpleSubscription(liveData: SingleRequestEvent<Void>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Result.Loading)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Result.Success(null, it.message.toString()))
        else liveData.postValue(Result.Error(it.message.toString()))
    }, { it ->
        val error = parseException(it);
        error.let {
            liveData.postValue(Result.Error(it))
        }

    })
}

fun <B> Single<B>.customSubscription(liveData: SingleRequestEvent<B>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Result.Loading)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        liveData.postValue(Result.Success(it, "Successful"))
    }, { it ->
        val error = parseException(it);
        error.let {
            liveData.postValue(Result.Error(it))
        }
    })
}

fun <M> Single<BaseApiResponse>.simpleSubscriptionWithTag(
    tag: M, liveData: SingleRequestEvent<M>
): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Result.Loading)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Result.Success(tag, it.message.toString()))
        else liveData.postValue(Result.Error(it.message.toString()))
    }, { it ->
        val error = parseException(it)
        error.let {
            liveData.postValue(Result.Error(it))
        }

    })
}

fun <T> SingleRequestEvent<T>.singleObserver(
    owner: LifecycleOwner,
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T) -> Unit),
    onError: ((throwable: Throwable, showError: Boolean) -> Unit),
) {
    this.observe(owner, object : SingleRequestEvent.RequestObserver<T> {
        override fun onRequestReceived(result: Result<T?>) {
            when (result) {
                is Result.Loading -> onLoading(true)
                is Result.Success -> {
                    onLoading(false)
                    result.data?.let { onSuccess(it) }
                }

                is Result.Error -> {
                    onLoading(false)
                    onError(Throwable(result.message), true)
                }
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