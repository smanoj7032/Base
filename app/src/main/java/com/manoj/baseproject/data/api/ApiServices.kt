package com.manoj.baseproject.data.api

import com.manoj.baseproject.data.bean.Posts
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {

    @GET("data/v1/post")
    suspend fun getPosts(
        @Query("page") page: Int?
    ): Posts

    @GET("data/v1/post")
    suspend fun getPost(
        @Query("id") id: String?
    ): Posts

    @GET("data/v1/post")
    suspend fun getPostSingle(
        @Query("id") id: String?
    ): Response<Posts>
}