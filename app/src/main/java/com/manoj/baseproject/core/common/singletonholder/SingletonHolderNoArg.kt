package com.manoj.baseproject.core.common.singletonholder

open class SingletonHolderNoArg<out T>(creator: () -> T) {

    private var creator: (() -> T)? = creator

    @Volatile
    private var instance: T? = null

    /**
     * Returns the singleton instance. If the instance is not yet created, it will be created.
     *
     * @return The singleton instance of type T.
     */
    fun getInstance(): T? = instance ?: synchronized(this) {
        val inst = instance ?: creator?.let { it() }.also { instance = it };creator = null;inst
    }
}