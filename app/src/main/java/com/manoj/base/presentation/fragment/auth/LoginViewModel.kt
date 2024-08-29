package com.manoj.base.presentation.fragment.auth

import androidx.databinding.ObservableField
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
   val dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    val fieldEmail by lazy { ObservableField("base@yopmail.com") }
    val fieldPass by lazy { ObservableField("Mind@123") }
}