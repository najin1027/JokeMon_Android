package com.joke.mon.feature.recent_joke.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentJokeDao
{
    @Transaction
    suspend fun addRecentJokeWithLimit(joke: RecentJoke) {
        insertRecentJoke(joke)
        cleanupOldJokes()
    }

    @Query("SELECT * FROM recent_jokes ORDER BY createdAt DESC LIMIT 20")
    fun getRecentJokes(): Flow<List<RecentJoke>>

    @Query("SELECT * FROM recent_jokes WHERE `key` = :key LIMIT 1")
    suspend fun getRecentJokeByKey(key: String): RecentJoke?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentJoke(joke: RecentJoke)

    @Query("DELETE FROM recent_jokes WHERE id NOT IN (SELECT id FROM recent_jokes ORDER BY createdAt DESC LIMIT 20)")
    suspend fun cleanupOldJokes()


}