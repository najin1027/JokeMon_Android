package com.joke.mon.feature.result_joke.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.joke.mon.core.util.Config.KEY_CURRENT_SELECTED_CHARACTER_INDEX
import com.joke.mon.core.util.Resource
import com.joke.mon.feature.result_joke.data.source.remote.api.ResultJokeApiService
import com.joke.mon.feature.result_joke.data.source.remote.dto.JokeRequest
import com.joke.mon.feature.result_joke.data.source.remote.dto.JokeResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ResultJokeRepositoryImpl @Inject constructor(
    private val resultJokeApiService: ResultJokeApiService
) : ResultJokeRepository
{
    override fun fetchJoke(prompt : String): Flow<Resource<JokeResponse>>  = flow {
        emit(Resource.Loading())

        try {
            val requestBody = JokeRequest(prompt = prompt)

            val response = resultJokeApiService.fetchJoke(requestBody)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = response.message() ?: "Error Code ${response.code()}"
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
}