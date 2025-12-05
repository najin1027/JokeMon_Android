package com.joke.mon.core.data.soruce.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joke.mon.feature.like.data.source.local.dao.LikeJokeDao
import com.joke.mon.feature.like.data.source.local.entity.LikeJoke
import com.joke.mon.feature.recent_joke.data.source.local.dao.RecentJokeDao
import com.joke.mon.feature.recent_joke.data.source.local.entity.RecentJoke

@Database(
    entities = [LikeJoke::class , RecentJoke::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun likeJokeDao(): LikeJokeDao
    abstract fun recentJokeDao() : RecentJokeDao
}