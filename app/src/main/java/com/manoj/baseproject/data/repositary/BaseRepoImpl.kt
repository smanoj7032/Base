package com.manoj.baseproject.data.repositary

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.manoj.baseproject.BuildConfig
import com.manoj.baseproject.data.bean.Posts
import com.manoj.baseproject.data.local.SharedPrefManager
import com.manoj.baseproject.data.repositary.pagingsource.PostsPagingSource
import com.manoj.baseproject.network.api.BaseApi
import com.manoj.baseproject.utils.executeApiCall
import com.manoj.baseproject.utils.helper.Resource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BaseRepoImpl @Inject constructor(
    private val apiService: BaseApi,
    private val sharedPrefManager: SharedPrefManager,
) : BaseRepo {

    fun getAuthToken(): String {
        return "Bearer " + sharedPrefManager.getAccessToken()
    }

    private fun getAppId(): String {
        return BuildConfig.APP_ID
    }

    override fun getPosts() = Pager(config = PagingConfig(
        pageSize = 10,
        enablePlaceholders = false,
    ),
        pagingSourceFactory = { PostsPagingSource(apiService, getAppId()) }).flow

    override  fun getPost(id: String): Single<Posts> {
        return  apiService.getPost(getAppId(), id)
    }
}
