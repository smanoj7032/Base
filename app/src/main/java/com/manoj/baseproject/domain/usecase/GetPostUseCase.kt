package com.manoj.baseproject.domain.usecase

import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.domain.repositary.BaseRepo
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostUseCase @Inject constructor(private val repository: BaseRepo) {
     operator fun invoke(id:String): Flow<Posts> = repository.getPost(id)
}
