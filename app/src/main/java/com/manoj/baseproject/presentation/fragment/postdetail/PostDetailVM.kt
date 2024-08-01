package com.manoj.baseproject.presentation.fragment.postdetail

import androidx.lifecycle.viewModelScope
import com.manoj.baseproject.core.utils.DispatchersProvider
import com.manoj.baseproject.core.utils.customSubscription
import com.manoj.baseproject.core.network.helper.Resource
import com.manoj.baseproject.core.network.helper.SingleRequestEvent
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.domain.usecase.GetPostUseCase
import com.manoj.baseproject.core.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class PostDetailVM @javax.inject.Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    val obrPosts by lazy { SingleRequestEvent<Posts>() }
    val posts by lazy { MutableStateFlow<Resource<Posts?>>(Resource.loading()) }

    fun getPost(id: String) =
        launchOnIO { getPostUseCase.invoke(id).customSubscription(posts, viewModelScope) }
}