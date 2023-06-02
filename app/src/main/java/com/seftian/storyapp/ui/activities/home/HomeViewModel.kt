package com.seftian.storyapp.ui.activities.home

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.local.StoriesEntity
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.mappers.toStory
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.UserStoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.domain.Story
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesApi: NotesApi,
    private val userDatabase: UserDatabase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _apiResponse = MutableLiveData<ApiResponse<UserStoryResponse>>()
    val apiResponse = _apiResponse

    private val _userStories = MutableLiveData<List<Story>>()
    val userStories = _userStories

    init {
        getAllStoriesFromLocal()

        if(_userStories.value == null){
            getAllStoriesFromRemote(1)
        }
    }

    fun getAllStoriesFromRemote(page: Int){

        _apiResponse.value = ApiResponse.Loading

        viewModelScope.launch {
            val response = try {
                notesApi.allStories(page)
            } catch (e: HttpException) {
                _apiResponse.value = ApiResponse.Error(e.message())
                return@launch
            } catch (e: IOException) {
                _apiResponse.value = ApiResponse.Error(e.message)
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                _apiResponse.value = ApiResponse.Success(response.body()!!)
                return@launch
            }

            val errorBody = response.errorBody()?.string()
            val errorMessage = Helper.extractErrorMessage(errorBody)

            _apiResponse.value = ApiResponse.Error(errorMessage)
        }
    }

    fun logout(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                userDatabase.dao.deleteUser()
            }
        }
    }

    private fun getAllStoriesFromLocal(){
        viewModelScope.launch(Dispatchers.IO) {
            val localStories = userDatabase.dao.storySource()
            val mappedToStory = localStories.map{story->
                story.toStory()
            }.toList()

            _userStories.postValue(mappedToStory)
        }
    }

    fun deleteAllStoriesFromLocal(){
        viewModelScope.launch(Dispatchers.IO) {
            userDatabase.dao.deleteStories()
        }
    }

    fun upsertStoriesToLocal(stories: List<StoriesEntity>){
        viewModelScope.launch(Dispatchers.IO) {
            userDatabase.dao.upsertStory(stories)
            getAllStoriesFromLocal()
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

}