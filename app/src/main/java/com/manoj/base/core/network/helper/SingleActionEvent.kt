package com.manoj.base.core.network.helper

import androidx.annotation.MainThread

class SingleActionEvent<T> : SingleLiveEvent<T>() {

    @MainThread
    fun call(v: T) {
        value = v
    }
}