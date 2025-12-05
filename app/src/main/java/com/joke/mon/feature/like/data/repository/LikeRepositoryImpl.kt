package com.joke.mon.feature.like.data.repository

import com.joke.mon.feature.like.data.source.local.dao.LikeJokeDao
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(private val likeJokeDao: LikeJokeDao)  : LikeRepository
{
    override fun getLikedJokesFlow(): Flow<List<LikeJoke>> {
        return likeJokeDao.getLikedJokes()
    }

    override suspend fun addJoke(likeJoke: LikeJoke) {
        likeJokeDao.insertJoke(likeJoke)
    }

    override suspend fun deleteJoke(likeJoke: LikeJoke) {
        likeJokeDao.deleteJoke(likeJoke)
    }

    override suspend fun deleteJokeByKey(key: String) {
        likeJokeDao.deleteJokeByKey(key)
    }

    override suspend fun isJokeLiked(key: String): Flow<Boolean> {
        return likeJokeDao.isJokeLiked(key)
    }

    override suspend fun getJokeByKey(key: String): LikeJoke? {
        return likeJokeDao.getJokeByKey(key)
    }
}