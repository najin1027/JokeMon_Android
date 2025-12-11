package com.joke.mon.core.data.soruce.remote.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevenLabsRequest(
    @SerialName("text")
    val text: String,
    @SerialName("voiceId")
    val voiceId: String,
    @SerialName("style")
    val style: Double,
    @SerialName("stability")
    val stability: Double
)
