package com.manoj.base.presentation.fragment.postdetail

import com.manoj.base.core.common.base.BaseViewModel
import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.data.bean.Posts
import com.manoj.base.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class PostDetailVM @javax.inject.Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    val posts: MutableStateFlow<Result<Posts?>> = MutableStateFlow(Result.Loading)
    fun getPost(id: String) {
        launchOnIO { getPostUseCase.invoke(id, posts, this) }
    }
}