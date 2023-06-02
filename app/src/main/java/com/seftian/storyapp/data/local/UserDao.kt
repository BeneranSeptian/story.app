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

    @Upsert
    suspend fun upsertStory(story: List<StoriesEntity>)

    @Query("SELECT * FROM storiesentity")
    fun storySource(): List<StoriesEntity>

    @Query("SELECT * FROM storiesentity WHERE id = :storyId")
    fun getStory(storyId: String): StoriesEntity

    @Query("DELETE from storiesentity")
    fun deleteStories()

    @Query("SELECT * FROM storiesentity ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomStory(): StoriesEntity
}