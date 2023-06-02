package com.seftian.storyapp.di.token

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProviderImpl @Inject constructor() : TokenProvider {
    private var token: String = ""

    override fun getToken(): String {
        return token
    }

    override fun setToken(token: String) {
        this.token = token
    }
}