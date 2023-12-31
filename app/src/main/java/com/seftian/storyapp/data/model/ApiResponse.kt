package com.seftian.storyapp.data.model

sealed class ApiResponse<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiResponse<T>()
    data class Error(val message: String?) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}