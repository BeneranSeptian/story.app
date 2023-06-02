package com.seftian.storyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoriesEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float?,
    val lon: Float?
    )