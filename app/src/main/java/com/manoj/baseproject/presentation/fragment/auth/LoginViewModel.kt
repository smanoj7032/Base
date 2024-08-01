package com.manoj.baseproject.presentation.fragment.auth

import android.os.SystemClock
import com.manoj.baseproject.core.utils.DispatchersProvider
import com.manoj.baseproject.core.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor( dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

}