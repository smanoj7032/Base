package com.manoj.baseproject.core.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.manoj.baseproject.BuildConfig
import com.manoj.baseproject.core.common.base.LoadingStateManager
import com.manoj.baseproject.core.network.helper.apihelper.HeaderInterceptor
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProviderImpl
import com.manoj.baseproject.core.utils.extension.dataStore
import com.manoj.baseproject.data.api.ApiServices
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
class ApplicationModule {

    @Provides
    @Singleton
    fun providesOkHttp(headerInterceptor: HeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .callTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build()
    }

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
    fun provideSharedPref(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.packageName, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider = DispatchersProviderImpl()


    @Singleton
    @Provides
    fun provideCoroutineScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        CoroutineScope(dispatchersProvider.getIO() + SupervisorJob())


    @Provides
    @Singleton
    fun provideLoadingStateManager(): LoadingStateManager = LoadingStateManager()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}