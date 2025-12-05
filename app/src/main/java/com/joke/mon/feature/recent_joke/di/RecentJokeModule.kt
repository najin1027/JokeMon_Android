package com.joke.mon.feature.recent_joke.di

import com.joke.mon.feature.recent_joke.data.repository.RecentJokeRepository
import com.joke.mon.feature.recent_joke.data.repository.RecentJokeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RecentJokeModule {

    @Binds
    @Singleton
    abstract fun bindRecentJokeRepository(recentJokeRepositoryImpl: RecentJokeRepositoryImpl) : RecentJokeRepository



}