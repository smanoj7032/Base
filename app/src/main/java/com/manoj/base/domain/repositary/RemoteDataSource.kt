package com.manoj.base.domain.repositary


import androidx.paging.PagingData
import com.manoj.base.data.bean.Post
import com.manoj.base.data.bean.Posts
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getPost(id:String): Flow<Posts?>
    fun getPosts(): Flow<PagingData<Post>>
}