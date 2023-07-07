package com.seftian.storyapp.ui.activities.home

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.seftian.storyapp.domain.StoryRepository
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesApi: NotesApi,
    private val userDatabase: UserDatabase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val repository = StoryRepository(userDatabase,notesApi)

    val userStories: LiveData<PagingData<StoryResponse>> = repository.getStory().cachedIn(viewModelScope)

    fun logout(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                userDatabase.dao.deleteUser()
            }
        }
    }

    fun deleteAllStoriesFromLocal(){
        viewModelScope.launch(Dispatchers.IO) {
            userDatabase.dao.deleteStories()
        }
    }

    fun deleteToken(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit {
                    clear()
                }
            }
        }
    }

    fun deleteRemoteKeys(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                userDatabase.remoteKeysDao.deleteRemoteKeys()
            }
        }
    }

}