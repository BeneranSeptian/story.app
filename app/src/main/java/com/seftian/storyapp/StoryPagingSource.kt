package com.seftian.storyapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import java.lang.Exception

class StoryPagingSource (
    val notesApi: NotesApi
): PagingSource<Int, StoryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let {anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        val position = params.key ?: INITIAL_PAGE
        val storyResponse = notesApi.allStories(position, SIZE_TO_LOAD)
        return try {
            LoadResult.Page(
                data = storyResponse.body()!!.listStory,
                prevKey = if (position == INITIAL_PAGE) null else position-1,
                nextKey = if (storyResponse.body()?.listStory.isNullOrEmpty()) null else position+1
            )
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }

    companion object {
        const val INITIAL_PAGE = 1
        const val SIZE_TO_LOAD = 10
    }

}