package com.seftian.storyapp.ui.activities.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.mappers.toStory
import com.seftian.storyapp.domain.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val userDatabase: UserDatabase,
): ViewModel() {

    private val _userStory = MutableLiveData<Story>()
    val userStory = _userStory


    fun getStory(storyId: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val storyEntity = userDatabase.dao.getStory(storyId)
                _userStory.postValue(storyEntity.toStory())
            }
        }
    }
}