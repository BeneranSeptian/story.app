package com.seftian.storyapp.ui.activities.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.model.ResponseModel
import com.seftian.storyapp.data.remote.RetrofitClient
import com.seftian.storyapp.data.model.SignUpModel
import com.seftian.storyapp.util.Helper.extractErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SignupViewModel: ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading

    private val _responseSignUp = MutableLiveData<ResponseModel>()
    val responseSignUp = _responseSignUp

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse = _errorResponse


    fun postSignUp(payload: SignUpModel) {
        viewModelScope.launch {
            _loading.value = true

            val response = try {
                RetrofitClient.getApiService().signUp(payload)
            }catch (e: HttpException) {
                _errorResponse.value = e.message()
                return@launch
            } catch (e: IOException) {
                _errorResponse.value = e.message
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                _loading.value = false
                _responseSignUp.value = response.body()
                return@launch
            }

            _loading.value = false
            val errorBody = response.errorBody()?.string()
            val errorMessage = extractErrorMessage(errorBody)

            _errorResponse.value = errorMessage

        }
    }

}