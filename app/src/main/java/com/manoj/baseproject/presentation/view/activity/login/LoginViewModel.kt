package com.manoj.baseproject.presentation.view.activity.login

import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor( dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) { }