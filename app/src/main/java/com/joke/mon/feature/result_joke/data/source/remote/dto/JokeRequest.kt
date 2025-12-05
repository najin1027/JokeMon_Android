package com.joke.mon.feature.result_joke.data.source.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
data class JokeRequest(
    @SerializedName("prompt")
    val prompt: String
)
