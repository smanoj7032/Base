package com.manoj.base.core.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.manoj.base.BuildConfig
import com.manoj.base.core.network.helper.apihelper.HeaderInterceptor
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.dataStore
import com.manoj.base.data.api.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProviderModule {

    @Provides
    @Singleton
    fun providesOkHttp(headerInterceptor: HeaderInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .callTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS).build()

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient).addConverterFactory(
            GsonConverterFactory.create()
        ).addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiServices =
        retrofit.create(ApiServices::class.java)

    @Provides
    @Singleton
    fun provideSharedPref(application: Application): SharedPreferences =
        application.getSharedPreferences(application.packageName, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        CoroutineScope(dispatchersProvider.getIO() + SupervisorJob())

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}