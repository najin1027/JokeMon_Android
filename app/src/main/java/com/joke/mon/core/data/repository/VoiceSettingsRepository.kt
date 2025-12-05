package com.joke.mon.core.data.repository

import com.joke.mon.core.data.model.VoiceConfig
import kotlinx.coroutines.flow.Flow

interface VoiceSettingsRepository
{
    val voiceConfigFlow : Flow<VoiceConfig>

    suspend fun saveVoiceConfig(config : VoiceConfig)
}