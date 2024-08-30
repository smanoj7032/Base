package com.manoj.base.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.data.bean.Posts
import com.manoj.base.domain.repositary.pagingsource.PostsPagingSource
import com.manoj.base.data.api.ApiServices
import com.manoj.base.domain.repositary.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiServices,
    private val dispatchersProvider: DispatchersProvider
) : RemoteDataSource {


    override fun getPosts() = Pager(config = PagingConfig(
        pageSize = 30,
        enablePlaceholders = false,
    ), pagingSourceFactory = {
        PostsPagingSource(
            apiService, dispatchersProvider
        )
    }).flow


    override suspend fun getPost(id: String): Flow<Posts?> {
        return flow { emit(apiService.getPost(id)) }
    }
}
