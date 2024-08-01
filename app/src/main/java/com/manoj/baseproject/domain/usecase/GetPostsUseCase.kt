package com.manoj.baseproject.domain.usecase

import androidx.paging.PagingData
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.domain.repositary.BaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val repository: BaseRepo) {
    operator fun invoke(): Flow<PagingData<Post>> = repository.getPosts()
}
