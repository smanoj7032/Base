package com.manoj.baseproject.domain.repositary.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.manoj.baseproject.core.utils.dispatchers.DispatchersProvider
import com.manoj.baseproject.data.api.ApiServices
import com.manoj.baseproject.data.bean.Post
import kotlinx.coroutines.withContext


class PostsPagingSource(
    private val remote: ApiServices,
    private val dispatchersProvider: DispatchersProvider
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: 1
        Log.d("Page---->>", "load: $page")

        return try {
            val posts = withContext(dispatchersProvider.getIO()) { remote.getPosts( page) }
            LoadResult.Page(
                data = posts.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (posts.data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
