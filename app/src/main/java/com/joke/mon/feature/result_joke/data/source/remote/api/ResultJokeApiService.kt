package com.joke.mon.feature.result_joke.data.source.remote.api

import androidx.annotation.Keep
import com.joke.mon.feature.result_joke.data.source.remote.dto.JokeRequest
import com.joke.mon.feature.result_joke.data.source.remote.dto.JokeResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
interface ResultJokeApiService
{
    @POST("askChatGPT")
    suspend fun fetchJoke(@Body request : JokeRequest) : Response<JokeResponse>
}