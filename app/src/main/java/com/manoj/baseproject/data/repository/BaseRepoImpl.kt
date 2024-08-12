package com.manoj.baseproject.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.manoj.baseproject.BuildConfig
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.domain.repositary.pagingsource.PostsPagingSource
import com.manoj.baseproject.data.api.ApiServices
import com.manoj.baseproject.domain.repositary.BaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class BaseRepoImpl @Inject constructor(
    private val apiService: ApiServices,
    private val sharedPrefManager: SharedPrefManager,
    private val coroutineScope: CoroutineScope,
    private val dispatchersProvider: DispatchersProvider
) : BaseRepo {

    fun getAuthToken(): String {
        return "Bearer " + sharedPrefManager.getAccessToken()
    }

    private fun getAppId(): String {
        return BuildConfig.APP_ID
    }

    override fun getPosts() = Pager(config = PagingConfig(
        pageSize = 30,
        enablePlaceholders = false,
    ),
        pagingSourceFactory = {
            PostsPagingSource(
                apiService,
                getAppId(),
                dispatchersProvider
            )
        }).flow


    override suspend fun getPost(id: String): Flow<Posts?> {
        return flow { emit(apiService.getPost(getAppId(), id)) }
    }
}
