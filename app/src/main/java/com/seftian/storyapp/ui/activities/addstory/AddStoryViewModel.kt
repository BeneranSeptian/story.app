package com.seftian.storyapp.ui.activities.addstory

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.ResponseModel
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val notesApi: NotesApi
): ViewModel() {

    private val _apiResponse = MutableLiveData<ApiResponse<ResponseModel>>()
    val apiResponse = _apiResponse

    private val _uriPhoto = MutableLiveData<Uri>()
    val uriPhoto = _uriPhoto

    private fun uploadStory(imageMultipart: MultipartBody.Part, description: RequestBody){
        viewModelScope.launch {

            val response = try {
                notesApi.addStory(imageMultipart, description)
            }catch (e: HttpException) {
                _apiResponse.value = ApiResponse.Error(e.message)
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

    fun compressAndUploadStory(photoFile: File, description: String) {
        viewModelScope.launch {
            val file = Helper.reduceFileImage(photoFile)

            _apiResponse.value = ApiResponse.Loading

            try {
                val desc = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                uploadStory(imageMultipart, desc)

            }catch (e: Exception){
                _apiResponse.value = ApiResponse.Error(e.message)
            }
        }
    }

    fun setUriPhoto(uri: Uri){
        viewModelScope.launch{
            Log.d("ini uri dijalanin", uri.toString())
            _uriPhoto.value = uri
        }
    }

}