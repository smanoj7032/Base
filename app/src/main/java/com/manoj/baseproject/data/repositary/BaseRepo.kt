package com.manoj.baseproject.data.repositary


import androidx.paging.PagingData
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.data.bean.Posts
import kotlinx.coroutines.flow.Flow

interface BaseRepo {
     fun getPost(id:String): Flow<Posts>
    fun getPosts(): Flow<PagingData<Post>>
}