package com.manoj.baseproject.presentation.view.activity.splash

import com.manoj.baseproject.data.repositary.BaseRepo
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val baseRepo: BaseRepo,dispatchersProvider: DispatchersProvider) : BaseViewModel(dispatchersProvider) {
}