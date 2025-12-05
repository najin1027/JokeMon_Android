package com.joke.mon.core.di

import android.content.Context
import androidx.room.Room
import com.joke.mon.core.data.soruce.local.AppDatabase
import com.joke.mon.feature.like.data.source.local.dao.LikeJokeDao
import com.joke.mon.feature.recent_joke.data.source.local.dao.RecentJokeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @Singleton
        fun provideLikeJokeDao(appDatabase: AppDatabase): LikeJokeDao {
            return appDatabase.likeJokeDao()
        }

        @Provides
        @Singleton
        fun provideRecentJokeDao(appDatabase: AppDatabase) : RecentJokeDao {
            return appDatabase.recentJokeDao()
        }
    }