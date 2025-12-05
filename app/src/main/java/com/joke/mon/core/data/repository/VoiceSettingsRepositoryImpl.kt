package com.joke.mon.core.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joke.mon.core.data.model.VoiceConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VoiceSettingsRepositoryImpl @Inject constructor(
    private val dataStore : DataStore<Preferences>
) : VoiceSettingsRepository
{
    private object PreferencesKeys {
        val VOICE_ID = intPreferencesKey("voice_id")
        val SPEED = floatPreferencesKey("voice_speed")
        val TONE = stringPreferencesKey("voice_tone")
    }

    override val voiceConfigFlow: Flow<VoiceConfig> = dataStore.data
        .map { preferences ->
            VoiceConfig(
                voiceId = preferences[PreferencesKeys.VOICE_ID] ?: 0,
                speed = preferences[PreferencesKeys.SPEED] ?: 1.0f,
                tone = preferences[PreferencesKeys.TONE] ?: "보통"
            )
        }

    override suspend fun saveVoiceConfig(config: VoiceConfig) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOICE_ID] = config.voiceId
            preferences[PreferencesKeys.SPEED] = config.speed
            preferences[PreferencesKeys.TONE] = config.tone
        }
    }
}