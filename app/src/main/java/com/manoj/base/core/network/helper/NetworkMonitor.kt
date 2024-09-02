package com.manoj.base.core.network.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.Str
import com.manoj.base.core.utils.extension.showErrorToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
) : DefaultLifecycleObserver {

    enum class NetworkState {
        Available, Lost
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var previousState: NetworkState? = null

    private val scope = CoroutineScope(dispatchersProvider.getIO() + SupervisorJob())

    val networkState: Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                handleNetworkAvailability(this@callbackFlow)
            }

            override fun onLost(network: Network) {
                handleNetworkLoss(this@callbackFlow)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(getInitialState()).isSuccess
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun handleNetworkAvailability(channel: SendChannel<NetworkState>) {
        if (previousState != NetworkState.Available) {
            scope.launch {
                if (hasActiveInternetConnection()) {
                    withContext(dispatchersProvider.getMain()) {
                        channel.trySend(NetworkState.Available).isSuccess
                        previousState = NetworkState.Available
                        Logger.d("Network state changed to: Available")
                    }
                }
            }
        }
    }

    private fun handleNetworkLoss(channel: SendChannel<NetworkState>) {
        if (previousState != NetworkState.Lost) {
            channel.trySend(NetworkState.Lost).isSuccess
            previousState = NetworkState.Lost
            //context.showErrorToast(context.getString(Str.slow_or_no_internet_access))
            Logger.d("Network state changed to: Lost")
        }
    }

    private fun getInitialState(): NetworkState =
        if (connectivityManager.activeNetwork != null) NetworkState.Available else NetworkState.Lost

    suspend fun hasActiveInternetConnection(): Boolean {
        val url = "https://www.google.com"
        val client = OkHttpClient.Builder().build()

        return withContext(dispatchersProvider.getIO()) {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    response.isSuccessful
                }
            } catch (e: Exception) {
                Logger.e("Error checking internet connection", e.message)
                false
            }
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        scope.cancel()
    }
}
