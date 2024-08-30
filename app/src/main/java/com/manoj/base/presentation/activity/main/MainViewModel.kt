package com.manoj.base.presentation.activity.main


import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.base.core.common.base.BaseViewModel
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.data.bean.Post
import com.manoj.base.data.local.DataStoreManager
import com.manoj.base.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
    val dispatchers: DispatchersProvider,
) : BaseViewModel(dispatchers) {
    val posts: Flow<PagingData<Post>> = getPostsUseCase.invoke().cachedIn(viewModelScope)


}