package com.manoj.baseproject.presentation.view.activity.main


import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.data.bean.PostItem
import com.manoj.baseproject.domain.usecase.GetPostsUseCase
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.DispatchersProvider
import com.manoj.baseproject.utils.helper.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {
    private val _postItems: MutableStateFlow<Resource<List<PostItem>?>> =
        MutableStateFlow(Resource.loading())
    val collectPosts: StateFlow<Resource<List<PostItem>?>> by lazy { _postItems }

    val posts: Flow<PagingData<Post>> = getPostsUseCase.invoke().cachedIn(viewModelScope)
}