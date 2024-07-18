package com.manoj.baseproject.data.repositary.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.network.api.BaseApi
import com.manoj.baseproject.utils.executeApiCall
import com.manoj.baseproject.utils.helper.Status
import com.manoj.baseproject.utils.isOnline
import com.manoj.baseproject.utils.parseException
import kotlinx.coroutines.flow.catch


class PostsPagingSource(
    private val remote: BaseApi, private val appId: String
) : PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: 1
        Log.d("Page---->>", "load: $page")
        if (MyApplication.instance.isOnline()) {
            val response = executeApiCall { remote.getPosts(appId, page) }

            var result: LoadResult<Int, Post> = LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )

            response.catch { exception ->
                result = LoadResult.Error(
                    Throwable(parseException(exception).ifEmpty { "Something went wrong" })
                )
            }.collect { state ->
                when (state.status) {
                    Status.SUCCESS -> {
                        result = LoadResult.Page(
                            data = state.data?.data ?: emptyList(),
                            prevKey = if (page == 1) null else page - 1,
                            nextKey = if (state.data?.data.isNullOrEmpty()) null else page + 1
                        )
                    }
                    Status.ERROR -> {
                        result = LoadResult.Error(
                            Throwable(state.message.ifEmpty { "Something went wrong" })
                        )
                    }
                    else -> {}
                }
            }

            return result
        } else {
            return LoadResult.Error(Throwable("Check internet connection"))
        }

    }
}

