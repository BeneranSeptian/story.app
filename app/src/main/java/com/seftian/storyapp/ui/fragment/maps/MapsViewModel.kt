package com.seftian.storyapp.ui.fragment.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.UserStoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val notesApi: NotesApi
) : ViewModel() {

    private val _storyWithLocation = MutableLiveData<ApiResponse<UserStoryResponse>>()
    val storyWithLocation = _storyWithLocation

    init {
        getStoryWithLocation(1)
    }

    fun getStoryWithLocation(page: Int){
        viewModelScope.launch {

            _storyWithLocation.value = ApiResponse.Loading

            try{
                val response = notesApi.getStoryWithLocation(1, page)

                if(response.isSuccessful && response.body() != null){
                    _storyWithLocation.value = ApiResponse.Success(response.body()!!)
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = Helper.extractErrorMessage(errorBody)
                    _storyWithLocation.value = ApiResponse.Error(errorMessage)
                }
            }catch (e:Exception){
                val errorMessage = e.message
                _storyWithLocation.value = ApiResponse.Error(errorMessage)
            }
        }
    }
}