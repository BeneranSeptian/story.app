package com.seftian.storyapp.ui.activities.login

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.local.UserEntity
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.di.token.TokenProvider
import com.seftian.storyapp.util.Helper.extractErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val notesApi: NotesApi,
    private val userDatabase: UserDatabase,
    private val sharedPreferences: SharedPreferences,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _apiResponse = MutableLiveData<ApiResponse<LoginResponse>>()
    val apiResponse: LiveData<ApiResponse<LoginResponse>> = _apiResponse

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn = _isLoggedIn

    init{
        viewModelScope.launch {
            val token = getTokenFromPref()
            _isLoggedIn.value = token.isNotEmpty()
            tokenProvider.setToken(token)
        }
    }

    fun postLogin(payload: LoginModel) {
        viewModelScope.launch {
            _apiResponse.value = ApiResponse.Loading

            try {
                val response = notesApi.login(payload)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    _apiResponse.value = ApiResponse.Success(loginResponse)
                    tokenProvider.setToken(loginResponse.loginResult.token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    _apiResponse.value = ApiResponse.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = e.message
                _apiResponse.value = ApiResponse.Error(errorMessage)
            }
        }
    }

    fun setTokenToPref(tokenUser: String){
        viewModelScope.launch {
            sharedPreferences.edit{
                putString("token", tokenUser)
                apply()
            }
        }
    }

    private fun getTokenFromPref(): String{
        val token = sharedPreferences.getString("token", null)

        if(!token.isNullOrEmpty()){
            return token
        }

        return ""
    }

    fun updateUserLoginData(user: UserEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDatabase.dao.upsertUser(user)
            }
        }
    }
}
