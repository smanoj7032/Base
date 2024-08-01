package com.manoj.baseproject.core.network.helper

import androidx.lifecycle.LifecycleOwner

class SingleRequestEvent<T> : SingleLiveEvent<Resource<T?>>() {

    interface RequestObserver<T> {
        fun onRequestReceived(resource: Resource<T?>)
    }

    fun observe(owner: LifecycleOwner, observer: RequestObserver<T>) {
        super.observe(owner) { resource ->
            resource.let {
                observer.onRequestReceived(it)
            }
        }
    }
}
