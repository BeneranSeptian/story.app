package com.seftian.storyapp.domain

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.seftian.storyapp.data.StoryMediator
import com.seftian.storyapp.data.StoryPagingSource.Companion.SIZE_TO_LOAD
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi

class StoryRepository (
    private val userDatabase: UserDatabase,
    private val notesApi: NotesApi,
    ) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStory() : LiveData<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = SIZE_TO_LOAD
            ),
            remoteMediator = StoryMediator(userDatabase, notesApi),
            pagingSourceFactory = {
                userDatabase.dao.getAllStories()
            }
        ).liveData
    }
}