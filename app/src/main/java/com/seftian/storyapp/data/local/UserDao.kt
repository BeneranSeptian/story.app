package com.seftian.storyapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("SELECT * FROM userentity")
    fun userSource(): UserEntity?

    @Query("DELETE from userentity")
    fun deleteUser()
}