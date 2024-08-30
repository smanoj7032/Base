package com.manoj.base.presentation.fragment.profile

import androidx.lifecycle.viewModelScope
import com.manoj.base.core.common.base.BaseViewModel
import com.manoj.base.core.common.sociallogin.googlelogin.GoogleSignInManager
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(dispatchersProvider: DispatchersProvider, dataStoreManager: DataStoreManager) :
    BaseViewModel(dispatchersProvider) {
    private val _user = MutableStateFlow<GoogleSignInManager.UserData?>(null)
    val user: StateFlow<GoogleSignInManager.UserData?> get() = _user

    init {
        dataStoreManager.getCurrentUser(GoogleSignInManager.UserData::class.java).onEach {
            _user.value = it
        }.launchIn(viewModelScope)
    }
}