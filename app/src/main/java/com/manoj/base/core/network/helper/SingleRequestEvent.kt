package com.manoj.base.core.network.helper

import androidx.lifecycle.LifecycleOwner
import com.manoj.base.core.network.helper.apihelper.Result

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
