package com.seftian.storyapp

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.seftian.storyapp.StoryPagingSource.Companion.SIZE_TO_LOAD
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi

class StoryRepository (
    private val notesApi: NotesApi,
    ) {

    fun getStory() : LiveData<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = SIZE_TO_LOAD
            ),
            pagingSourceFactory = { StoryPagingSource(notesApi) }
        ).liveData
    }

    fun resetStory(){
        StoryPagingSource(notesApi).invalidate()
    }
}