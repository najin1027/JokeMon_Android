package com.joke.mon.feature.home.di

import com.joke.mon.feature.home.data.repository.HomeRepository
import com.joke.mon.feature.home.data.repository.HomeRepositoryImpl
import com.joke.mon.feature.home.data.source.remote.api.HomeApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    companion object {
        @Provides
        @Singleton
        fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
            return retrofit.create(HomeApiService::class.java)
        }
    }
}