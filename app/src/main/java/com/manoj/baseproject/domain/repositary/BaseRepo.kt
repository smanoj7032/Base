package com.manoj.baseproject.domain.repositary


import androidx.paging.PagingData
import com.manoj.baseproject.core.network.helper.Result
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.data.bean.Posts
import kotlinx.coroutines.flow.Flow

interface BaseRepo {
    suspend fun getPost(id:String): Flow<Result<Posts?>>
    fun getPosts(): Flow<PagingData<Post>>
}