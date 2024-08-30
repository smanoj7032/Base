package com.manoj.base.domain.usecase

import androidx.paging.PagingData
import com.manoj.base.data.bean.Post
import com.manoj.base.domain.repositary.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val repository: RemoteDataSource) {
    operator fun invoke(): Flow<PagingData<Post>> = repository.getPosts()
}
