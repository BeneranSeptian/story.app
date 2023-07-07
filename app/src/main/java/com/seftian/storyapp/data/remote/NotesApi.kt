package com.seftian.storyapp.data.remote

import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.data.model.ResponseModel
import com.seftian.storyapp.data.model.SignUpModel
import com.seftian.storyapp.data.model.UserStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface NotesApi {
    @POST("register")
    suspend fun signUp(@Body requestBody: SignUpModel): Response<ResponseModel>

    @POST("login")
    suspend fun login(@Body requestBody: LoginModel): Response<LoginResponse>

    @GET("stories")
    suspend fun allStories(
        @Query("page")page: Int?,
        @Query("size")size: Int?,
    ):Response<UserStoryResponse>

    @GET("stories")
    suspend fun getStoryWithLocation(
        @Query("location")location: Int,
        @Query("page")page: Int?,
    ):Response<UserStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(@Part file: MultipartBody.Part,
                         @Part("description") description: RequestBody,
                         @Part("lat") lat: RequestBody?,
                         @Part("lon") lon: RequestBody?
    ):Response<ResponseModel>
    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
    }
}