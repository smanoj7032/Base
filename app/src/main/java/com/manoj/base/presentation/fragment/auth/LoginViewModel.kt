package com.manoj.base.presentation.fragment.auth

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.common.base.BaseViewModel
import com.manoj.base.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    dataStoreManager: DataStoreManager
) : BaseViewModel(dispatchers) {

    val fieldEmail by lazy { ObservableField("base@yopmail.com") }
    val fieldPass by lazy { ObservableField("Mind@123") }
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> get() = _accessToken

    init {
        observeAccessToken(dataStoreManager)
    }

    private fun observeAccessToken(dataStoreManager: DataStoreManager) {
        dataStoreManager.accessTokenFlow
            .onEach { token -> _accessToken.value = token }
            .launchIn(viewModelScope)
    }
}
