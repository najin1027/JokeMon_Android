package com.joke.mon.feature.recent_joke.data.repository

import com.joke.mon.feature.recent_joke.data.source.local.dao.RecentJokeDao
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecentJokeRepositoryImpl @Inject constructor(
    private val recentJokeDao: RecentJokeDao
) : RecentJokeRepository
{
    override suspend fun addRecentJokeWithLimit(joke: RecentJoke) {
        recentJokeDao.addRecentJokeWithLimit(joke)
    }

    override suspend fun getRecentJokes(): Flow<List<RecentJoke>> {
       return recentJokeDao.getRecentJokes()
    }

    override suspend fun insertRecentJoke(joke: RecentJoke) {
       recentJokeDao.insertRecentJoke(joke)
    }

    override suspend fun cleanupOldJokes() {
        recentJokeDao.cleanupOldJokes()
    }

    override suspend fun getRecentJokeByKey(jokeKey : String): RecentJoke? {
       return recentJokeDao.getRecentJokeByKey(jokeKey)
    }


}