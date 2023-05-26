package com.seftian.storyapp.data.mappers

import com.seftian.storyapp.data.local.UserEntity
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.domain.User


fun LoginResponse.toUserEntity() : UserEntity {
    return UserEntity(
        userId = loginResult.userId,
        name = loginResult.name,
        token = loginResult.token,
    )
}

fun UserEntity.toUser() : User? {
    return User(
        userId, name, token
    )
}