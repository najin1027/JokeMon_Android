package com.joke.mon.feature.splash.di

import com.joke.mon.feature.splash.data.repository.SplashRepository
import com.joke.mon.feature.splash.data.repository.SplashRepositoryImpl
import com.joke.mon.feature.splash.data.source.remote.api.SplashApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashModule {

    @Binds
    @Singleton
    abstract fun bindSplashRepository(
       splashRepositoryImpl: SplashRepositoryImpl
    ): SplashRepository

     companion object {
         @Provides
         @Singleton
         fun provideSplashApiService(retrofit: Retrofit): SplashApiService {
             return retrofit.create(SplashApiService::class.java)
         }
     }
}

