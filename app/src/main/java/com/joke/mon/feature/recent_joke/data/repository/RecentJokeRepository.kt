package com.joke.mon.feature.recent_joke.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import kotlinx.coroutines.flow.Flow

interface RecentJokeRepository
{
    suspend fun addRecentJokeWithLimit(joke: RecentJoke)

    suspend fun getRecentJokes(): Flow<List<RecentJoke>>

    suspend fun insertRecentJoke(joke: RecentJoke)

    suspend fun cleanupOldJokes()

    suspend fun getRecentJokeByKey(jokeKey : String) : RecentJoke?
}