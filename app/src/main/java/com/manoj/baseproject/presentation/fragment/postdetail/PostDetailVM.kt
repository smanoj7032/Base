package com.manoj.baseproject.presentation.fragment.postdetail

import com.manoj.baseproject.core.common.base.BaseViewModel
import com.manoj.baseproject.core.network.helper.Result
import com.manoj.baseproject.core.network.helper.SystemVariables.isInternetConnected
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class PostDetailVM @javax.inject.Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    val posts: MutableStateFlow<Result<Posts?>> =
        MutableStateFlow(if (isInternetConnected) Result.Loading else Result.Error("Slow or no Internet Access"))

    fun getPost(id: String) {
        launchOnIO { getPostUseCase.invoke(id, posts, this) }
    }
}