package com.manoj.baseproject.core.network.helper

import androidx.lifecycle.LifecycleOwner

class SingleRequestEvent<T> : SingleLiveEvent<Result<T?>>() {

    interface RequestObserver<T> {
        fun onRequestReceived(result: Result<T?>)
    }

    fun observe(owner: LifecycleOwner, observer: RequestObserver<T>) {
        super.observe(owner) { result ->
            result.let {
                observer.onRequestReceived(it)
            }
        }
    }
}
