package com.manoj.baseproject.data.api

import com.manoj.baseproject.data.bean.Posts
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface BaseApi {

    @GET("data/v1/post")
    suspend fun getPosts(
        @Header("app-id") token: String,
        @Query("page") page: Int?
    ): Response<Posts>

    @GET("data/v1/post")
   suspend  fun getPost(
        @Header("app-id") token: String,
        @Query("id") id: String?
    ):Posts
}