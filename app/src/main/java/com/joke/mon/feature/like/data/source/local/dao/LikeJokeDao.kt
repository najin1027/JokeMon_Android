package com.joke.mon.feature.like.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeJokeDao {
    @Query("SELECT * FROM like_jokes ORDER BY createdAt DESC")
    fun getLikedJokes(): Flow<List<LikeJoke>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoke(likeJoke: LikeJoke)

    @Delete
    suspend fun deleteJoke(likeJoke: LikeJoke)

    @Query("DELETE FROM like_jokes WHERE `key` = :key")
    suspend fun deleteJokeByKey(key: String)

    @Query("SELECT EXISTS(SELECT 1 FROM like_jokes WHERE `key` = :key)")
    fun isJokeLiked(key: String): Flow<Boolean>

    @Query("SELECT * FROM like_jokes WHERE `key` = :key LIMIT 1")
    suspend fun getJokeByKey(key: String): LikeJoke?

}