package com.manoj.baseproject.core.network.helper

import com.manoj.baseproject.BuildConfig
import com.manoj.baseproject.data.local.SharedPrefManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(private val sharedPrefManager: SharedPrefManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("app-id", BuildConfig.APP_ID)
            .build()
        return chain.proceed(request)
    }
}