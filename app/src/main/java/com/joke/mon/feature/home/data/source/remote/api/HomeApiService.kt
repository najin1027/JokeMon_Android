package com.joke.mon.feature.home.data.source.remote.api

import androidx.annotation.Keep
import com.joke.mon.feature.home.data.source.remote.dto.KeywordResponse
import retrofit2.Response
import retrofit2.http.GET

interface HomeApiService
{
    @GET("getKeywords")
    suspend fun fetchRecommendKeyword(): Response<KeywordResponse>
}