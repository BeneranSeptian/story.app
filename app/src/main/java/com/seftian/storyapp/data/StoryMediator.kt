package com.seftian.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.seftian.storyapp.StoryPagingSource.Companion.INITIAL_PAGE
import com.seftian.storyapp.StoryPagingSource.Companion.SIZE_TO_LOAD
import com.seftian.storyapp.data.local.RemoteKeys
import com.seftian.storyapp.data.local.StoriesEntity
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.mappers.toStoriesEntity
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val userDatabase: UserDatabase,
    private val notesApi: NotesApi
): RemoteMediator<Int, StoryResponse>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryResponse>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

            try {
                val responseData = notesApi.allStories(page, SIZE_TO_LOAD)
                val listStory = responseData.body()?.listStory
                val endOfPaginationReached = listStory?.isEmpty()

                userDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        userDatabase.remoteKeysDao.deleteRemoteKeys()
                        userDatabase.dao.deleteStories()
                    }
                    if (!listStory.isNullOrEmpty()) {

                        val prevKey = if (page == 1) null else page - 1
                        val nextKey = if (endOfPaginationReached == true) null else page + 1
                        val keys = listStory.map {
                            RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                        }
                        userDatabase.remoteKeysDao.insertAll(keys)

                        val storyToInsert = ArrayList<StoriesEntity>()
                        listStory.forEach {
                            storyToInsert.add(it.toStoriesEntity())
                        }
                        userDatabase.dao.upsertStory(storyToInsert)
                    }
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached!!)
            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }

        private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryResponse>): RemoteKeys? {
            return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { data ->
                    userDatabase.remoteKeysDao.getRemoteKeysId(data.id)
                }
        }

        private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryResponse>): RemoteKeys? {
            return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { data ->
                    userDatabase.remoteKeysDao.getRemoteKeysId(data.id)
                }
        }

        private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryResponse>): RemoteKeys? {
            return state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    userDatabase.remoteKeysDao.getRemoteKeysId(id)
                }
            }
        }
}