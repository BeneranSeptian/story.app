package com.seftian.storyapp.ui.activities.home

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.seftian.storyapp.StoryRepository
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.model.UserStoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.domain.Story
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

    private val repository = StoryRepository(notesApi)

    private val _apiResponse = MutableLiveData<ApiResponse<UserStoryResponse>>()
    val apiResponse = _apiResponse

    private val _userStories = MutableLiveData<List<Story>>()
    val userStories = _userStories

    val _testingStory: LiveData<PagingData<StoryResponse>> = repository.getStory().cachedIn(viewModelScope)

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

    fun resetStory(){
        repository.resetStory()
    }

}