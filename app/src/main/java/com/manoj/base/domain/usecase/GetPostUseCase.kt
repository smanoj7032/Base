package com.manoj.base.domain.usecase

import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.defaultEmitter
import com.manoj.base.data.bean.Posts
import com.manoj.base.domain.repositary.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val repository: RemoteDataSource,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        id: String,
        posts: MutableStateFlow<Result<Posts?>>,
        viewModelScope: CoroutineScope
    ) = repository.getPost(id).defaultEmitter(posts, viewModelScope, dispatchersProvider)
}
