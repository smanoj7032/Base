package com.manoj.base.presentation.fragment.profile

import com.manoj.base.core.common.base.BaseViewModel
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(dispatchersProvider: DispatchersProvider) :
    BaseViewModel(dispatchersProvider) {}