package com.joke.mon.feature.home.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.joke.mon.core.util.Config.KEY_CURRENT_SELECTED_CHARACTER_INDEX
import javax.inject.Inject
import androidx.core.content.edit
import com.joke.mon.R
import com.joke.mon.core.util.Config.KEY_NICK_NAME
import com.joke.mon.core.util.Resource
import com.joke.mon.feature.home.data.source.remote.api.HomeApiService
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class HomeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val homeApiService: HomeApiService) : HomeRepository
{
    override  fun getKeyword(): Flow<Resource<KeywordResponse>>  = flow {
        emit(Resource.Loading())

        try {
            val response = homeApiService.fetchRecommendKeyword()
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error(message = context.getString(R.string.error_unknown)))
            } else {
                val errorCode = response.code()
                val errorMessage = response.message()
                emit(Resource.Error(message = "Error $errorCode: $errorMessage"))
            }
        } catch (e: IOException) {
            emit(Resource.Error(message = context.getString(R.string.error_internet_connect)))
        } catch (e: Exception) {
            emit(Resource.Error(message = context.getString(R.string.error_unknown)))
        }
    }


}