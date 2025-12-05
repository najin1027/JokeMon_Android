package com.joke.mon.feature.search.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.joke.mon.core.util.Config.KEY_CURRENT_SELECTED_CHARACTER_INDEX
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val sharedPreferences: SharedPreferences ,
) : SearchRepository
{

}