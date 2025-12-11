package com.joke.mon.feature.result_joke.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JokeRequest(
    @SerialName("prompt")
    val prompt: String
)
