package com.manoj.base.presentation.fragment.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.data.bean.Post
import com.manoj.base.domain.usecase.GetPostsUseCase
import com.manoj.base.core.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class HomeVM @javax.inject.Inject constructor(
    getPostsUseCase: GetPostsUseCase, dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    val posts: Flow<PagingData<Post>> = getPostsUseCase.invoke().cachedIn(viewModelScope)
}