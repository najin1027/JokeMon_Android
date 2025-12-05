package com.joke.mon.core.di

import android.content.Context
import android.content.SharedPreferences
import com.joke.mon.core.data.repository.SharePreferenceRepository
import com.joke.mon.core.data.repository.SharedPreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("local_preference", Context.MODE_PRIVATE)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {
        @Binds
        @Singleton
        abstract fun bindSharedPreferenceRepository(impl : SharedPreferenceRepositoryImpl) : SharePreferenceRepository
    }
}