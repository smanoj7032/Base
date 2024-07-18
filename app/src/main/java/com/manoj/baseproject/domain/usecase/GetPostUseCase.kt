package com.manoj.baseproject.domain.usecase

import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.data.repositary.BaseRepo
import com.manoj.baseproject.utils.helper.Resource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostUseCase @Inject constructor(private val repository: BaseRepo) {
     operator fun invoke(id:String): Single<Posts> = repository.getPost(id)
}
