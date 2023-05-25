package com.seftian.storyapp.ui.activities.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.data.remote.RetrofitClient
import com.seftian.storyapp.util.Helper.extractErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.reflect.Constructor
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(private val notesApi: NotesApi): ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading

    private val _responseLogin = MutableLiveData<LoginResponse>()
    val responseLogin = _responseLogin

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse = _errorResponse

    fun postLogin(payload: LoginModel) {
        viewModelScope.launch {
            _loading.value = true

            val response = try {
                notesApi.login(payload)
            } catch (e: HttpException) {
                _errorResponse.value = e.message()
                return@launch
            } catch (e: IOException) {
                _errorResponse.value = e.message
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                _loading.value = false
                _responseLogin.value = response.body()
                return@launch
            }

            _loading.value = false
            val errorBody = response.errorBody()?.string()
            val errorMessage = extractErrorMessage(errorBody)

            _errorResponse.value = errorMessage

        }
    }
}