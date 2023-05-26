package com.seftian.storyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    @PrimaryKey
    val userId: String,
    val name: String,
    val token: String,
)