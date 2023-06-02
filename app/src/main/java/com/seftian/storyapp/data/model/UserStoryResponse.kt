package com.seftian.storyapp.data.model

data class UserStoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryResponse>
)

data class StoryResponse(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float?,
    val lon: Float?
)