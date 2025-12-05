package com.joke.mon.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SharePreferenceRepository
{
    suspend fun saveNickname(nickname : String)
    suspend fun getNickname() : String
    suspend fun saveSelectedCharacterIndex(index : Int)
    suspend fun getCurrentSelectedCharacterIndex() : Int
    suspend fun getNotificationIsEnabled() : Boolean
    suspend fun saveNotificationIsEnabled(isEnable : Boolean)

    fun observeCurrentSelectedCharacterIndex(): Flow<Int>

    suspend fun saveLastReviewDialogTime(timeMillis: Long)
    suspend fun getLastReviewDialogTime(): Long
}