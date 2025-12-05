package com.joke.mon.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.joke.mon.core.data.repository.VoiceSettingsRepository
import com.joke.mon.core.data.repository.VoiceSettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

object DataStoreModule
{
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voice_settings")

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStoreModule {

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {
        @Binds
        @Singleton
        abstract fun bindVoiceSettingsRepository(
            impl: VoiceSettingsRepositoryImpl
        ): VoiceSettingsRepository
    }
}
