package com.seftian.storyapp.ui.activities.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.ResponseModel
import com.seftian.storyapp.data.model.SignUpModel
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.util.Helper.extractErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val notesApi: NotesApi): ViewModel() {

    private val _apiResponse = MutableLiveData<ApiResponse<ResponseModel>>()
    val apiResponse = _apiResponse


    fun postSignUp(payload: SignUpModel) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading

            val response = try {
                notesApi.signUp(payload)
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
            val errorMessage = extractErrorMessage(errorBody)

            _apiResponse.value = ApiResponse.Error(errorMessage)
        }
    }

}