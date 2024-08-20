package com.manoj.baseproject.presentation.activity.main


import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.domain.usecase.GetPostsUseCase
import com.manoj.baseproject.core.common.base.BaseViewModel
import com.manoj.baseproject.core.utils.extension.Ids
import com.manoj.baseproject.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
    dispatchers: DispatchersProvider, private val dataStoreManager: DataStoreManager
) : BaseViewModel(dispatchers) {
    private val _startDestination = MutableStateFlow(Ids.loginFragment)
    val startDestination: StateFlow<Int> = _startDestination.asStateFlow()
    val posts: Flow<PagingData<Post>> = getPostsUseCase.invoke().cachedIn(viewModelScope)

    init {
        launchOnIO {
            dataStoreManager.accessTokenFlow
                .map { if (it.isNullOrEmpty()) Ids.loginFragment else Ids.homeFragment }
                .collect { _startDestination.value = it }
        }
    }
}