package com.manoj.base.core.common.singletonholder

open class SingletonHolder<out T, in A>(private val creator: (A) -> T) {
    @Volatile
    private var singletonInstance: T? = null

    fun getInstance(arg: A): T {
        return singletonInstance ?: synchronized(this) {
            val syncInstance = singletonInstance ?: creator(arg).also { singletonInstance = it };syncInstance
        }
    }
}

