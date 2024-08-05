package com.manoj.baseproject.domain.usecase

import com.manoj.baseproject.core.network.helper.Result
import com.manoj.baseproject.core.utils.extension.emitter
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.domain.repositary.BaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetPostUseCase @Inject constructor(private val repository: BaseRepo) {
     suspend operator fun invoke(
          id: String,
          posts: MutableStateFlow<Result<Posts?>>,
          viewModelScope: CoroutineScope
     )= repository.getPost(id).emitter(posts,viewModelScope)
}
