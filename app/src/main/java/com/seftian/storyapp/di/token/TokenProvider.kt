package com.seftian.storyapp.di.token

interface TokenProvider {
    fun getToken(): String
    fun setToken(token: String)
}