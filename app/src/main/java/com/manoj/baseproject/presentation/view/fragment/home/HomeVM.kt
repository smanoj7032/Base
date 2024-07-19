package com.manoj.baseproject.presentation.view.fragment.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.domain.usecase.GetPostsUseCase
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class HomeVM @javax.inject.Inject constructor(
    getPostsUseCase: GetPostsUseCase, dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    val posts: Flow<PagingData<Post>> = getPostsUseCase.invoke().cachedIn(viewModelScope)
}