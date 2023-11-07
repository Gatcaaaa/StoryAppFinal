package com.submisson.aleggappstory.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.submisson.aleggappstory.data.response.ListStoryItem
import com.submisson.aleggappstory.data.retrofit.ApiService

class PagingSource(private val token: String, private val apiService: ApiService):
    PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(token, page, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (page == 1) null else page -1,
                nextKey = if (responseData.isNullOrEmpty()) null else page +1
            )
        } catch (e: Exception){
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX =1
    }


}