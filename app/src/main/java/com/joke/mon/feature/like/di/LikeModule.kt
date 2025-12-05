package com.joke.mon.feature.like.di

import com.joke.mon.feature.like.data.repository.LikeRepository
import com.joke.mon.feature.like.data.repository.LikeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LikeModule {

    @Binds
    @Singleton
    abstract fun bindLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository
}