package com.manoj.baseproject.core.network.helper

import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class SingleMessageEvent : MutableLiveData<Any>() {

    interface MessageObserver {
        fun onMessageReceived(@StringRes msgResId: Int)
        fun onMessageReceived(msg: String)
    }

    fun observe(owner: LifecycleOwner, observer: MessageObserver) {
        super.observe(owner) { t ->
            try {
                when (t) {
                    is String -> observer.onMessageReceived(t)
                    is Int -> observer.onMessageReceived(t)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
