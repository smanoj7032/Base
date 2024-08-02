package com.manoj.baseproject.domain.repositary.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.baseproject.MyApplication
import com.manoj.baseproject.core.utils.extension.executeApiCall
import com.manoj.baseproject.core.utils.extension.isOnline
import com.manoj.baseproject.core.utils.extension.parseException
import com.manoj.baseproject.data.api.ApiServices
import com.manoj.baseproject.data.bean.Post
import kotlinx.coroutines.flow.catch
import com.manoj.baseproject.core.network.helper.Result


class PostsPagingSource(
    private val remote: ApiServices, private val appId: String
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
                when (state) {
                    is Result.Success -> {
                        result = LoadResult.Page(
                            data = state.data?.data ?: emptyList(),
                            prevKey = if (page == 1) null else page - 1,
                            nextKey = if (state.data?.data.isNullOrEmpty()) null else page + 1
                        )
                    }

                    is Result.Error -> {
                        result = LoadResult.Error(
                            Throwable(state.message.ifEmpty { "Something went wrong" })
                        )
                    }

                    is Result.Loading -> {}
                }
            }

            return result
        } else {
            return LoadResult.Error(Throwable("Check internet connection"))
        }

    }
}

