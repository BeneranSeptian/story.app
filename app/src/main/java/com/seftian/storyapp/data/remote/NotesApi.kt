package com.seftian.storyapp.data.remote

import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.data.model.ResponseModel
import com.seftian.storyapp.data.model.SignUpModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotesApi {
    @POST("register")
    suspend fun signUp(@Body requestBody: SignUpModel): Response<ResponseModel>

    @POST("login")
    suspend fun login(@Body requestBody: LoginModel): Response<LoginResponse>

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
    }
}