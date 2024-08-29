package com.manoj.base.core.network.helper.apihelper

import com.manoj.base.BuildConfig
import com.manoj.base.data.local.SharedPrefManager
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