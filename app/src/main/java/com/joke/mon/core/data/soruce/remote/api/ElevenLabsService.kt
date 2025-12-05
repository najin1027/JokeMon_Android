package com.joke.mon.core.data.soruce.remote.api

import androidx.annotation.Keep
import com.joke.mon.core.data.soruce.remote.dto.ElevenLabsRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
interface ElevenLabsService
{
    @POST("elevenLabsTTS")
    suspend fun generateSpeech(@Body request: ElevenLabsRequest): ResponseBody
}
