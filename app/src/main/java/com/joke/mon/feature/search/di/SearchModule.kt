package com.joke.mon.feature.search.di

import com.joke.mon.feature.home.data.repository.HomeRepository
import com.joke.mon.feature.home.data.repository.HomeRepositoryImpl
import com.joke.mon.feature.search.data.repository.SearchRepository
import com.joke.mon.feature.search.data.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
}