package com.joke.mon.core.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.joke.mon.core.util.Config.KEY_CURRENT_SELECTED_CHARACTER_INDEX
import com.joke.mon.core.util.Config.KEY_LAST_REVIEW_DIALOG_TIME
import com.joke.mon.core.util.Config.KEY_NICK_NAME
import com.joke.mon.core.util.Config.KEY_NOTIFICATION_IS_ENABLED
import com.joke.mon.core.util.Utils.intFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedPreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences ,
) : SharePreferenceRepository
{
    override suspend fun saveNickname(nickname: String) {
        sharedPreferences.edit{ putString(KEY_NICK_NAME , nickname)}
    }

    override suspend fun getNickname(): String {
        return sharedPreferences.getString(KEY_NICK_NAME , "") ?:""
    }

    override suspend fun getCurrentSelectedCharacterIndex(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_SELECTED_CHARACTER_INDEX , 0)
    }

    override suspend fun saveSelectedCharacterIndex(index: Int) {
        sharedPreferences.edit { putInt(KEY_CURRENT_SELECTED_CHARACTER_INDEX , index) }
    }

    override suspend fun getNotificationIsEnabled(): Boolean  = sharedPreferences.getBoolean(KEY_NOTIFICATION_IS_ENABLED  , true)

    override suspend fun saveNotificationIsEnabled(isEnable: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_NOTIFICATION_IS_ENABLED , isEnable) }
    }

    override fun observeCurrentSelectedCharacterIndex(): Flow<Int> {
        return sharedPreferences.intFlow(KEY_CURRENT_SELECTED_CHARACTER_INDEX , 0)
    }

    override suspend fun saveLastReviewDialogTime(timeMillis: Long) {
        sharedPreferences.edit {
            putLong(KEY_LAST_REVIEW_DIALOG_TIME, timeMillis)
        }
    }

    override suspend fun getLastReviewDialogTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_REVIEW_DIALOG_TIME, 0L)
    }
}