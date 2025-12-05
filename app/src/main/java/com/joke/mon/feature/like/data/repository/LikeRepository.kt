package com.joke.mon.feature.like.data.repository

import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import kotlinx.coroutines.flow.Flow

interface LikeRepository
{
    fun getLikedJokesFlow(): Flow<List<LikeJoke>>

    suspend fun addJoke(likeJoke: LikeJoke)

    suspend fun deleteJoke(likeJoke: LikeJoke)

    suspend fun deleteJokeByKey(key: String)

    suspend fun isJokeLiked(key: String): Flow<Boolean>

    suspend fun getJokeByKey(key : String) : LikeJoke?
}