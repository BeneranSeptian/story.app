package com.seftian.storyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, StoriesEntity::class, RemoteKeys::class],
    version = 2
)
abstract class UserDatabase: RoomDatabase() {
    abstract val dao: UserDao
    abstract val remoteKeysDao: RemoteKeysDao
}