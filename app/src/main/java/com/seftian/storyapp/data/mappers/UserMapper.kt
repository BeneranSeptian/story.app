package com.seftian.storyapp.data.mappers

import com.seftian.storyapp.data.local.StoriesEntity
import com.seftian.storyapp.data.local.UserEntity
import com.seftian.storyapp.data.model.LoginResponse
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.domain.Story
import com.seftian.storyapp.domain.User


fun LoginResponse.toUserEntity() : UserEntity {
    return UserEntity(
        userId = loginResult.userId,
        name = loginResult.name,
        token = loginResult.token,
    )
}

fun UserEntity.toUser() : User {
    return User(
        userId, name, token
    )
}

fun StoryResponse.toStoriesEntity(): StoriesEntity {
    return StoriesEntity(
        id,
        name,
        description,
        photoUrl,
        createdAt,
        lat,
        lon
    )
}

fun StoriesEntity.toStory(): Story {
    return Story(
        id,
        name,
        description,
        photoUrl,
        createdAt,
        lat,
        lon
    )
}