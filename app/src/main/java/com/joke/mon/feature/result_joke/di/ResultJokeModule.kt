package com.joke.mon.feature.result_joke.di


import com.joke.mon.feature.result_joke.data.repository.ResultJokeRepository
import com.joke.mon.feature.result_joke.data.repository.ResultJokeRepositoryImpl
import com.joke.mon.feature.result_joke.data.source.remote.api.ResultJokeApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResultJokeModule {


    @Binds
    @Singleton
    abstract fun bindResultJokeRepository(resultJokeRepositoryImpl: ResultJokeRepositoryImpl) : ResultJokeRepository

    companion object {
        @Provides
        @Singleton
        fun provideResultJokeApiService(retrofit: Retrofit): ResultJokeApiService {
            return retrofit.create(ResultJokeApiService::class.java)
        }
    }
}